"""
================================================================================
train_model.py  —  Training model deteksi diskalkulia dari data sintetis
================================================================================
Fitur (sama dengan yang dihasilkan modul serving features.py):
    age, std_dot_enum, std_stroop, std_addition, std_multiplication, has_mult

Tiga bagian:
  1. DEMONSTRATOR SIRKULARITAS  — decision tree dangkal di ruang stanine.
     Menunjukkan secara EKSPLISIT bahwa label deterministik dari aturan
     Butterworth, sehingga model "menemukan kembali" aturan itu. (untuk laporan)
  2. MODEL DEPLOYABLE           — di fitur std_* dgn class_weight balanced;
     metrik per-kelas + confusion matrix (bukan akurasi mentah).
  3. ANALISIS ROBUSTNESS        — tambahkan noise pengukuran ke fitur test;
     ukur degradasi. Ini bagian yang bermakna: menunjukkan margin keputusan,
     bukan sekadar hafalan.
================================================================================
"""
import numpy as np
import pandas as pd
import joblib
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier, export_text
from sklearn.metrics import classification_report, confusion_matrix, f1_score

RNG = 42
FEATURES = ["age", "std_dot_enum", "std_stroop", "std_addition",
            "std_multiplication", "has_mult"]
LABELS = ["typical", "low_achievement", "dyscalculia"]

# ------------------------------------------------------------------ load & prep
df = pd.read_csv("dyscalculia_synthetic.csv")
df["has_mult"] = (~df["std_multiplication"].isna()).astype(int)
df["std_multiplication"] = df["std_multiplication"].fillna(100.0)  # netral (stanine 5)

X = df[FEATURES].to_numpy()
y = df["label"].to_numpy()

# split stratified by label -> minoritas pasti terwakili di test
X_tr, X_te, y_tr, y_te, idx_tr, idx_te = train_test_split(
    X, y, df.index, test_size=0.25, stratify=y, random_state=RNG)

print("="*72)
print(f"Train: {len(X_tr)}  |  Test: {len(X_te)}")
print("Distribusi test:", dict(pd.Series(y_te).value_counts()))

# ============================================================================
# 1. DEMONSTRATOR SIRKULARITAS  (di ruang stanine, tree dangkal & interpretable)
# ============================================================================
print("\n" + "="*72)
print("[1] DEMONSTRATOR SIRKULARITAS — decision tree di fitur stanine")
print("="*72)
stan_cols = ["stanine_dot_enum","stanine_stroop","stanine_addition","stanine_multiplication"]
S = df[stan_cols].fillna(9).to_numpy()   # mult hilang -> stanine 9 (tidak rendah)
S_tr, S_te = S[idx_tr], S[idx_te]

demo = DecisionTreeClassifier(max_depth=4, random_state=RNG)
demo.fit(S_tr, y_tr)
acc_demo = demo.score(S_te, y_te)
print(f"Akurasi tree dangkal di ruang stanine: {acc_demo:.4f}")
print("\nAturan yang 'ditemukan kembali' oleh tree (cuplikan):")
rules = export_text(demo, feature_names=stan_cols, max_depth=3)
print("\n".join(rules.splitlines()[:18]))
print("\n>> Interpretasi: label DETERMINISTIK dari stanine kapasitas. Model tidak")
print(">> menemukan pengetahuan baru; ia memformalkan aturan skrining Butterworth.")

# ============================================================================
# 2. MODEL DEPLOYABLE  (fitur std_*, penanganan imbalance)
# ============================================================================
print("\n" + "="*72)
print("[2] MODEL DEPLOYABLE — fitur std_*, class_weight='balanced'")
print("="*72)

models = {
    "LogisticRegression": LogisticRegression(
        class_weight="balanced", max_iter=2000, random_state=RNG),
    "RandomForest": RandomForestClassifier(
        n_estimators=300, max_depth=None, class_weight="balanced",
        random_state=RNG, n_jobs=-1),
}

results = {}
for name, mdl in models.items():
    mdl.fit(X_tr, y_tr)
    pred = mdl.predict(X_te)
    macro = f1_score(y_te, pred, average="macro")
    results[name] = (mdl, macro)
    print(f"\n--- {name}  (macro-F1 = {macro:.4f}) ---")
    print(classification_report(y_te, pred, labels=LABELS, digits=3, zero_division=0))

best_name = max(results, key=lambda k: results[k][1])
best_model = results[best_name][0]
print(f"Model terbaik: {best_name}")
print("Confusion matrix (baris=aktual, kolom=prediksi):")
cm = confusion_matrix(y_te, best_model.predict(X_te), labels=LABELS)
print(pd.DataFrame(cm, index=LABELS, columns=LABELS).to_string())

plt.figure(figsize=(8,6))
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=LABELS, yticklabels=LABELS)
plt.title(f'Confusion Matrix: {best_name}')
plt.ylabel('Aktual')
plt.xlabel('Prediksi')
plt.tight_layout()
plt.savefig('confusion_matrix.png', dpi=300)
plt.close()

# ============================================================================
# 3. ANALISIS ROBUSTNESS  (noise pengukuran -> degradasi)
# ============================================================================
print("\n" + "="*72)
print("[3] ANALISIS ROBUSTNESS — noise pengukuran pada fitur std_* saat test")
print("="*72)
print("(std score punya SD populasi = 15; sigma dinyatakan dalam poin std score)\n")
print(f"{'sigma':>6} | {'macro-F1':>9} | {'recall dyscalculia':>18} | {'recall low_ach':>14}")
print("-"*60)

std_cols_idx = [FEATURES.index(c) for c in ["std_dot_enum","std_stroop","std_addition","std_multiplication"]]
rng = np.random.default_rng(RNG)

sigma_vals, macro_vals, recall_dys_vals, recall_low_vals = [], [], [], []

for sigma in [0, 3, 6, 9, 12, 15]:
    Xn = X_te.copy()
    noise = rng.normal(0, sigma, size=(len(Xn), len(std_cols_idx)))
    Xn[:, std_cols_idx] += noise
    pred = best_model.predict(Xn)
    macro = f1_score(y_te, pred, average="macro")
    rep = classification_report(y_te, pred, labels=LABELS, output_dict=True, zero_division=0)
    print(f"{sigma:6d} | {macro:9.4f} | {rep['dyscalculia']['recall']:18.3f} | {rep['low_achievement']['recall']:14.3f}")
    
    sigma_vals.append(sigma)
    macro_vals.append(macro)
    recall_dys_vals.append(rep['dyscalculia']['recall'])
    recall_low_vals.append(rep['low_achievement']['recall'])

plt.figure(figsize=(8,5))
plt.plot(sigma_vals, macro_vals, marker='o', label='Macro-F1')
plt.plot(sigma_vals, recall_dys_vals, marker='s', label='Recall: Dyscalculia')
plt.plot(sigma_vals, recall_low_vals, marker='^', label='Recall: Low Achievement')
plt.title('Ketahanan Model Terhadap Noise (Error Pengukuran)')
plt.xlabel('Gangguan (Poin Standar)')
plt.ylabel('Skor Metrik')
plt.ylim(0.4, 1.05)
plt.legend()
plt.grid(True, linestyle='--', alpha=0.6)
plt.tight_layout()
plt.savefig('robustness_chart.png', dpi=300)
plt.close()

print("\n>> Interpretasi: tanpa noise, performa mendekati sempurna (konsekuensi")
print(">> sirkularitas). Kurva degradasi menunjukkan seberapa besar margin keputusan")
print(">> bertahan terhadap error pengukuran nyata di lapangan — INI nilai analisisnya.")

# ------------------------------------------------------------------ save model
joblib.dump({"model": best_model, "features": FEATURES, "labels": LABELS},
            "model.joblib")
print("\n" + "="*72)
print(f"Model deployable ({best_name}) disimpan -> model.joblib")
print("Fitur (urutan penting untuk serving):", FEATURES)
