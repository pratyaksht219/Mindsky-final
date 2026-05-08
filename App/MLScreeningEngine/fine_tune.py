"""
Fine-tune the all-MiniLM-L6-v2 Sentence Transformer using Contrastive Learning.

This script generates positive/negative sentence pairs from DOMAIN_PROTOTYPES
and fine-tunes the model so that:
  - Sentences from the SAME domain are pushed CLOSER in embedding space.
  - Sentences from DIFFERENT domains are pushed APART in embedding space.

Key improvements over naive contrastive training:
  - All domains are equalized to 15 examples each.
  - Hard negative pairs are prioritized for commonly confused domain boundaries.
  - Conservative hyperparameters (low lr, fewer epochs) to prevent overfitting.

Usage:
    python fine_tune.py
    python fine_tune.py --epochs 2 --batch_size 16 --lr 5e-6
"""

import os
import itertools
import random
import argparse
from sentence_transformers import SentenceTransformer, InputExample, losses
from torch.utils.data import DataLoader
from prototypes import DOMAIN_PROTOTYPES

FINE_TUNED_MODEL_DIR = "./fine_tuned_model"
BASE_MODEL = "sentence-transformers/all-MiniLM-L6-v2"

# Domain pairs that are commonly confused by the model.
# Hard negatives from these pairs will be FULLY included (not randomly sampled).
HARD_NEGATIVE_PAIRS = [
    ("DEPRESSION", "STRESS"),
    ("DEPRESSION", "DISTRESS_GENERAL"),
    ("DEPRESSION", "SOCIAL_SUPPORT"),
    ("ANXIETY", "SOCIAL_ANXIETY"),
    ("ANXIETY", "TRAUMA"),
    ("ANXIETY", "DISTRESS_GENERAL"),
    ("STRESS", "DISTRESS_GENERAL"),
    ("STRESS", "ADHD"),
]


def generate_training_pairs():
    """
    Generate contrastive training pairs from DOMAIN_PROTOTYPES.

    Returns a list of InputExample objects:
      - Positive pairs (label=1.0): two sentences from the SAME domain.
      - Hard negative pairs (label=0.0): ALL cross-domain pairs for confused boundaries.
      - Random negative pairs (label=0.0): sampled from remaining cross-domain combos.
    """
    training_pairs = []
    domains = list(DOMAIN_PROTOTYPES.keys())
    hard_neg_set = set(
        (min(a, b), max(a, b)) for a, b in HARD_NEGATIVE_PAIRS
    )

    # --- Positive pairs: all combinations within each domain ---
    for domain in domains:
        sentences = DOMAIN_PROTOTYPES[domain]
        for s1, s2 in itertools.combinations(sentences, 2):
            training_pairs.append(
                InputExample(texts=[s1, s2], label=1.0)
            )

    positive_count = len(training_pairs)

    # --- Hard negative pairs: FULL inclusion for confused domain boundaries ---
    hard_negative_pairs = []
    random_negative_pool = []

    for i, d1 in enumerate(domains):
        for d2 in domains[i + 1:]:
            pair_key = (min(d1, d2), max(d1, d2))
            pairs_for_combo = []
            for s1 in DOMAIN_PROTOTYPES[d1]:
                for s2 in DOMAIN_PROTOTYPES[d2]:
                    pairs_for_combo.append(
                        InputExample(texts=[s1, s2], label=0.0)
                    )

            if pair_key in hard_neg_set:
                hard_negative_pairs.extend(pairs_for_combo)
            else:
                random_negative_pool.extend(pairs_for_combo)

    # --- Random negative pairs: sample to balance total negatives ~ total positives ---
    remaining_budget = max(0, positive_count - len(hard_negative_pairs))
    random.seed(42)
    if len(random_negative_pool) > remaining_budget:
        sampled_random = random.sample(random_negative_pool, remaining_budget)
    else:
        sampled_random = random_negative_pool

    all_negatives = hard_negative_pairs + sampled_random
    training_pairs.extend(all_negatives)

    # Shuffle all pairs
    random.seed(42)
    random.shuffle(training_pairs)

    print(f"  Positive pairs (same domain): {positive_count}")
    print(f"  Hard negative pairs (confused boundaries): {len(hard_negative_pairs)}")
    print(f"  Random negative pairs (other boundaries): {len(sampled_random)}")
    print(f"  Total training pairs: {len(training_pairs)}")

    return training_pairs


def fine_tune(epochs=2, batch_size=16, learning_rate=5e-6, warmup_ratio=0.1):
    """
    Fine-tune the sentence transformer model using CosineSimilarityLoss
    with conservative hyperparameters to prevent overfitting.
    """
    # Verify domain balance
    counts = {d: len(s) for d, s in DOMAIN_PROTOTYPES.items()}
    print("Domain prototype counts:")
    for d, c in counts.items():
        print(f"  {d}: {c}")
    if len(set(counts.values())) > 1:
        print("  WARNING: Domains are not balanced! Consider equalizing.")

    print(f"\nLoading base model: {BASE_MODEL}")
    model = SentenceTransformer(BASE_MODEL)

    print("Generating contrastive training pairs from prototypes...")
    train_examples = generate_training_pairs()

    # Create DataLoader
    train_dataloader = DataLoader(
        train_examples,
        shuffle=True,
        batch_size=batch_size
    )

    # Define loss function
    train_loss = losses.CosineSimilarityLoss(model=model)

    # Calculate warmup steps
    total_steps = len(train_dataloader) * epochs
    warmup_steps = int(total_steps * warmup_ratio)

    print(f"\nTraining configuration:")
    print(f"  Epochs: {epochs}")
    print(f"  Batch size: {batch_size}")
    print(f"  Learning rate: {learning_rate}")
    print(f"  Total steps: {total_steps}")
    print(f"  Warmup steps: {warmup_steps}")
    print(f"  Output directory: {FINE_TUNED_MODEL_DIR}")

    print("\nStarting fine-tuning...")

    # Fine-tune the model
    model.fit(
        train_objectives=[(train_dataloader, train_loss)],
        epochs=epochs,
        warmup_steps=warmup_steps,
        optimizer_params={"lr": learning_rate},
        output_path=FINE_TUNED_MODEL_DIR,
        show_progress_bar=True
    )

    print(f"\nFine-tuned model saved to: {os.path.abspath(FINE_TUNED_MODEL_DIR)}")
    print("Done! You can now run evaluate.py to check the new accuracy.")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Fine-tune Sentence Transformer with Contrastive Learning"
    )
    parser.add_argument(
        "--epochs", type=int, default=2,
        help="Number of training epochs (default: 2)"
    )
    parser.add_argument(
        "--batch_size", type=int, default=16,
        help="Training batch size (default: 16)"
    )
    parser.add_argument(
        "--lr", type=float, default=5e-6,
        help="Learning rate (default: 5e-6)"
    )
    args = parser.parse_args()

    fine_tune(epochs=args.epochs, batch_size=args.batch_size, learning_rate=args.lr)
