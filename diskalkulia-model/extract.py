"""
================================================================================
Dyscalculia Screener - Synthetic Data Generator (Fase 2-4)
================================================================================
Membangun dataset individual sintetis dari norma agregat (kuartil + median)
hasil digitisasi manual Butterworth (2003) Dyscalculia Screener.

Alur:
  Fase 2  : parameter distribusi per usia per subtes (log-normal dari kuartil)
  Fase 3  : sampling individu via Gaussian copula (marginals: log-normal RT,
            Beta accuracy) supaya struktur korelasi antar-subtes terjaga
  Fase 4  : efficiency measure -> standarisasi empiris -> stanine -> label

PENTING (untuk laporan):
  Dataset ini SINTETIS-dari-norma. Label dihasilkan oleh aturan Butterworth,
  sehingga model yang dilatih di sini memvalidasi KONSISTENSI terhadap aturan
  skrining, bukan realitas klinis. Semua asumsi ditandai dengan [ASUMSI].
================================================================================
"""

import numpy as np
import pandas as pd
from scipy import stats

RNG = np.random.default_rng(42)

# ==============================================================================
# FASE 1 (input): tabel hasil digitisasi. Format: usia -> (q_fast, median, q_slow)
# q_fast = ms terendah (tercepat), q_slow = ms tertinggi (terlambat).
# ==============================================================================
RT_CHARTS = {
    "srt": {
        6: (385, 440, 530),
        7: (345, 405, 490),
        8: (315, 370, 450),
        9: (290, 340, 410),
        10: (270, 315, 375),
        11: (250, 290, 345),
        12: (240, 275, 320),
        13: (235, 260, 300),
        14: (235, 250, 285),
    },
    "dot_enum": {
        6: (3450, 4400, 5300),
        7: (3000, 3800, 4650),
        8: (2600, 3300, 4050),
        9: (2250, 2850, 3500),
        10: (1900, 2450, 3000),
        11: (1650, 2100, 2550),
        12: (1450, 1850, 2200),
        13: (1300, 1600, 1900),
        14: (1200, 1400, 1700),
    },
    "stroop": {
        6: (1080, 1660, 2260),
        7: (920, 1260, 1700),
        8: (800, 1030, 1380),
        9: (690, 880, 1140),
        10: (610, 750, 940),
        11: (520, 660, 790),
        12: (450, 570, 680),
        13: (390, 510, 580),
        14: (350, 460, 600),
    },
    "addition": {
        6: (3600, 5700, 10000),
        7: (3300, 4900, 8300),
        8: (3000, 4100, 6800),
        9: (2700, 3500, 5400),
        10: (2300, 3000, 4200),
        11: (2000, 2500, 3300),
        12: (1700, 2100, 2700),
        13: (1400, 1800, 2400),
        14: (1100, 1650, 2200),
    },
    "multiplication": {
        10: (2650, 3650, 4650),
        11: (2050, 2800, 3550),
        12: (1450, 2200, 2800),
        13: (1150, 1850, 2350),
        14: (1120, 1680, 2350),
    },
}

# Table 3: rata-rata proporsi benar. Usia tersedia: 6,7,8,10,12,14 (interpolasi 9,11,13).
ACC_RAW = {
    "dot_enum": {6: 0.91, 7: 0.91, 8: 0.91, 10: 0.91, 12: 0.91, 14: 0.93},
    "stroop": {6: 0.80, 7: 0.83, 8: 0.88, 10: 0.90, 12: 0.93, 14: 0.95},
    "addition": {6: 0.65, 7: 0.79, 8: 0.82, 10: 0.89, 12: 0.92, 14: 0.89},
    "multiplication": {10: 0.78, 12: 0.88, 14: 0.88},
}

# Figure 1: batas standard score -> stanine (interval kanan-terbuka).
STANINE_CUTS = [74, 82, 89, 97, 104, 112, 119, 127]  # <74=>1, 74-81=>2, ... , >=127=>9
FIG1_PCT = [4, 7, 12, 17, 20, 17, 12, 7, 4]  # persentase acuan (untuk validasi)

CAPACITY = ["dot_enum", "stroop"]  # tes kapasitas (inti aturan Butterworth)
ACHIEVEMENT = ["addition", "multiplication"]  # tes pencapaian

# ---- [ASUMSI] parameter yang TIDAK ada di grafik, wajib didokumentasikan -----
ACC_KAPPA = 45.0  # [ASUMSI] konsentrasi Beta accuracy (makin besar = sebaran individu makin kecil)
RHO_CAPACITY = (
    0.55  # [ASUMSI] korelasi antar dua tes kapasitas; menaikkan prevalensi ke ~4-6%
)
RHO_CROSS = 0.30  # [ASUMSI] korelasi kapasitas<->achievement (manual: dot~math .369, stroop~math .332)
RHO_RT_ACC = (
    -0.25
)  # [ASUMSI] korelasi RT<->akurasi dalam individu (lebih cepat cenderung sedikit kurang akurat)

# ==============================================================================
# FASE 2: parameter distribusi
# ==============================================================================
Q = 0.6744897501960817  # z untuk persentil 75 (asumsi q_fast/q_slow = Q1/Q3)


def lognorm_from_quartiles(q_fast, median, q_slow):
    """Parameter log-normal dari kuartil. mu=ln(median); sigma dari IQR log-space."""
    mu = np.log(median)
    sigma = np.log(q_slow / q_fast) / (2 * Q)
    return mu, sigma


def interp_acc(subtest, age):
    """Interpolasi linear akurasi untuk usia yang tak tercantum (9,11,13)."""
    d = ACC_RAW[subtest]
    xs = np.array(sorted(d))
    ys = np.array([d[x] for x in xs])
    return float(np.interp(age, xs, ys))


def beta_from_mean(mean_p, kappa=ACC_KAPPA):
    """Parameter Beta(a,b) dari mean + konsentrasi kappa."""
    return mean_p * kappa, (1 - mean_p) * kappa


# ==============================================================================
# FASE 3: sampling individu via Gaussian copula
# ==============================================================================
def build_corr_matrix(dims):
    """Matriks korelasi di ruang copula. dims = daftar nama dimensi terurut."""
    n = len(dims)
    R = np.eye(n)

    def idx(name):
        return dims.index(name)

    def set_(a, b, r):
        if a in dims and b in dims:
            R[idx(a), idx(b)] = R[idx(b), idx(a)] = r

    # korelasi RT antar-subtes
    set_("rt_dot_enum", "rt_stroop", RHO_CAPACITY)
    for cap in CAPACITY:
        for ach in ACHIEVEMENT:
            set_(f"rt_{cap}", f"rt_{ach}", RHO_CROSS)
    set_("rt_addition", "rt_multiplication", RHO_CROSS)
    # korelasi RT<->akurasi (subtes yang sama)
    for s in CAPACITY + ACHIEVEMENT:
        set_(f"rt_{s}", f"acc_{s}", RHO_RT_ACC)
    return R


def nearest_psd(R):
    """Perbaiki matriks agar positive semi-definite (Higham sederhana via clipping eigen)."""
    vals, vecs = np.linalg.eigh(R)
    vals = np.clip(vals, 1e-8, None)
    R2 = vecs @ np.diag(vals) @ vecs.T
    d = np.sqrt(np.diag(R2))
    R2 = R2 / np.outer(d, d)
    return R2


def sample_age(age, n):
    """Generate n individu untuk satu pita usia."""
    subtests = [
        s
        for s in ["dot_enum", "stroop", "addition", "multiplication"]
        if age in RT_CHARTS[s]
    ]
    dims = [f"rt_{s}" for s in subtests] + [f"acc_{s}" for s in subtests]
    R = nearest_psd(build_corr_matrix(dims))

    # 1) sampel gaussian berkorelasi -> uniform (copula)
    Z = RNG.multivariate_normal(np.zeros(len(dims)), R, size=n)
    U = stats.norm.cdf(Z)

    out = {"age": np.full(n, age)}
    # 2) inversi marginal
    for j, dim in enumerate(dims):
        kind, sub = dim.split("_", 1)
        if kind == "rt":
            mu, sig = lognorm_from_quartiles(*RT_CHARTS[sub][age])
            out[dim] = np.exp(stats.norm.ppf(U[:, j], loc=mu, scale=sig))
        else:  # acc
            a, b = beta_from_mean(interp_acc(sub, age))
            out[dim] = stats.beta.ppf(U[:, j], a, b)
    # SRT baseline (independen; tak punya akurasi)
    mu, sig = lognorm_from_quartiles(*RT_CHARTS["srt"][age])
    out["rt_srt"] = RNG.lognormal(mu, sig, size=n)
    return pd.DataFrame(out)


# ==============================================================================
# FASE 4: efficiency measure -> stanine -> label
# ==============================================================================
def efficiency(df, subtest):
    """eff = (RT_subtes - SRT) / proporsi_benar. Nilai besar = buruk (lambat/tak akurat)."""
    rt = df[f"rt_{subtest}"].to_numpy()
    srt = df["rt_srt"].to_numpy()
    acc = df[f"acc_{subtest}"].to_numpy().clip(0.05, 1.0)
    return (rt - srt).clip(1.0, None) / acc


def to_stanine(std_score):
    return np.digitize(std_score, STANINE_CUTS) + 1  # <74 ->1 ... >=127 ->9


def process(df):
    """Hitung efficiency, standarisasi empiris per usia, stanine per subtes."""
    subtests = ["dot_enum", "stroop", "addition", "multiplication"]
    for s in subtests:
        col = f"rt_{s}"
        if col not in df:
            continue
        eff = efficiency(df, s)
        df[f"eff_{s}"] = eff
        std = np.full(len(df), np.nan)
        # Standarisasi rank-based (van der Waerden) DALAM pita usia.
        # Memetakan distribusi efficiency (yang miring) ke normal(100,15) persis,
        # meniru cara tes norm-referenced menghasilkan distribusi stanine baku.
        # Tanda dibalik: efficiency tinggi (buruk) -> standard score rendah.
        for age in df["age"].unique():
            idx = np.where(df["age"].to_numpy() == age)[0]
            e = eff[idx]
            mask = ~np.isnan(e)
            if mask.sum() < 2:
                continue
            ii = idx[mask]
            q = stats.rankdata(e[mask]) / (mask.sum() + 1)  # kuantil (0,1)
            std[ii] = 100 + 15 * stats.norm.ppf(1 - q)  # kecil eff -> skor tinggi
        df[f"std_{s}"] = std
        df[f"stanine_{s}"] = np.where(np.isnan(std), np.nan, to_stanine(std))
    return df


def label_butterworth(df):
    """
    Aturan pola:
      - stanine<=2 di KEDUA tes kapasitas -> 'dyscalculia'
      - kapasitas normal tapi achievement rendah -> 'low_achievement'
      - selain itu -> 'typical'
    """
    st_dot = df["stanine_dot_enum"]
    st_str = df["stanine_stroop"]
    cap_low = (st_dot <= 2) & (st_str <= 2)

    # achievement rendah: addition ATAU multiplication stanine<=2 (jika ada)
    ach_low = df.get("stanine_addition", pd.Series(9, index=df.index)) <= 2
    if "stanine_multiplication" in df:
        ach_low = ach_low | (df["stanine_multiplication"].fillna(9) <= 2)
    cap_ok = ~cap_low

    label = np.where(
        cap_low, "dyscalculia", np.where(cap_ok & ach_low, "low_achievement", "typical")
    )
    df["label"] = label
    return df


# ==============================================================================
# MAIN
# ==============================================================================
def generate(n_per_age=2000):
    ages = list(range(6, 15))
    df = pd.concat([sample_age(a, n_per_age) for a in ages], ignore_index=True)
    df = process(df)
    df = label_butterworth(df)
    return df


if __name__ == "__main__":
    df = generate(n_per_age=2000)

    print("=" * 70)
    print(f"Total individu sintetis : {len(df)}")
    print(
        f"Kolom fitur             : {[c for c in df.columns if c.startswith(('rt_','acc_','eff_','std_','stanine_'))][:6]} ..."
    )
    print("\nDistribusi label:")
    print(df["label"].value_counts(normalize=True).round(4).to_string())

    print(
        "\n[VALIDASI] Distribusi stanine dot_enum vs acuan Figure 1 (4,7,12,17,20,17,12,7,4):"
    )
    dist = (
        df["stanine_dot_enum"].value_counts(normalize=True).sort_index() * 100
    ).round(1)
    print("  stanine :", [int(x) for x in dist.index.tolist()])
    print("  model % :", dist.values.tolist())
    print("  acuan % :", FIG1_PCT)

    print("\n[VALIDASI] Prevalensi diskalkulia (target literatur ~4-6%):")
    print(f"  {(df['label']=='dyscalculia').mean()*100:.2f}%")

    df.to_csv("dyscalculia_synthetic.csv", index=False)
    print("\nDisimpan -> dyscalculia_synthetic.csv")
