# Fine-Tuning the Sentence Transformer for Ambiguous Edge Cases

## Problem

The current `all-MiniLM-L6-v2` model is **pre-trained on generic internet text**. Its embedding space has no concept of your specific domain boundaries. When a sentence like _"I feel like a burden"_ is embedded, it lands in a region of the vector space where DEPRESSION and SOCIAL_SUPPORT overlap — because the pre-trained model was never taught that "burden" in a mental health context should lean toward depression.

**The only way to fix this is to fine-tune the model's internal weights** so the embedding space itself is reshaped around your specific domain boundaries.

## Proposed Approach: Contrastive Learning

The industry-standard technique for fine-tuning Sentence Transformers is **Contrastive Learning** using pairs/triplets of sentences.

### How it works:
1. We generate **training pairs** from your existing `prototypes.py` data:
   - **Positive pairs**: Two sentences from the **same** domain (e.g., two DEPRESSION sentences) → model learns to push them **closer** together.
   - **Negative pairs**: Two sentences from **different** domains (e.g., one DEPRESSION + one SOCIAL_SUPPORT) → model learns to push them **further apart**.
2. The model's internal neural network weights are updated via backpropagation so that the 384-dimensional embedding space is reshaped to better separate your domains.
3. The fine-tuned model is saved locally (to `./fine_tuned_model/`) and used instead of the Hugging Face download.

### Why Contrastive Learning over a Classification Head?
- A classification head would turn this into a rigid 10-class classifier. If you add a new domain later, you'd need to retrain.
- Contrastive learning preserves the flexible cosine-similarity architecture you already have. You can still add new domains by just adding prototype sentences — the embedding space itself is just *better*.

## Proposed Changes

### [NEW] [fine_tune.py](file:///Users/pratyakshtrivedi/Desktop/Mindsky/App/MLScreeningEngine/fine_tune.py)
A standalone training script that:
1. Generates all possible positive/negative sentence pairs from `prototypes.py`.
2. Uses `sentence-transformers`' built-in `InputExample` and `CosineSimilarityLoss` for training.
3. Fine-tunes `all-MiniLM-L6-v2` for a few epochs.
4. Saves the fine-tuned model to `./fine_tuned_model/`.

---

### [MODIFY] [classifier.py](file:///Users/pratyakshtrivedi/Desktop/Mindsky/App/MLScreeningEngine/classifier.py)
Update to load the fine-tuned model from `./fine_tuned_model/` if it exists, otherwise fall back to the Hugging Face pre-trained model. This is a backwards-compatible change — the service continues to work even without fine-tuning.

---

### [MODIFY] [.gitignore](file:///Users/pratyakshtrivedi/Desktop/Mindsky/App/MLScreeningEngine/.gitignore)
Add `fine_tuned_model/` to `.gitignore` since the model binary (~80MB) should not be committed to Git.

## Verification Plan

### Automated Tests
1. Run `python3.11 fine_tune.py` to train and save the model.
2. Re-run `python3.11 evaluate.py` to compare the new accuracy against the 97.5% baseline.
3. Specifically verify that the 2 previously-misclassified edge cases now pass:
   - `"I feel like a burden to everyone around me."` → should predict DEPRESSION
   - `"I cancelled my plans because the thought of a party makes me nauseous."` → should predict SOCIAL_ANXIETY

> [!IMPORTANT]
> Fine-tuning will modify the model weights. The fine-tuned model will be saved as a separate local directory (`fine_tuned_model/`), so the original pre-trained model is never touched. You can always revert by deleting that directory.
