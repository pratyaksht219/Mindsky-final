from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
import os
from prototypes import DOMAIN_PROTOTYPES

FINE_TUNED_MODEL_DIR = "./fine_tuned_model"
BASE_MODEL = "sentence-transformers/all-MiniLM-L6-v2"

# Load fine-tuned model if available, otherwise fall back to pre-trained
if os.path.exists(FINE_TUNED_MODEL_DIR):
    print(f"Loading fine-tuned model from: {FINE_TUNED_MODEL_DIR}")
    model = SentenceTransformer(FINE_TUNED_MODEL_DIR)
else:
    print(f"Fine-tuned model not found. Loading pre-trained: {BASE_MODEL}")
    model = SentenceTransformer(BASE_MODEL)

# build prototype embeddings
prototype_vectors = {}

for domain, examples in DOMAIN_PROTOTYPES.items():

    embeddings = model.encode(examples)

    prototype_vectors[domain] = np.mean(embeddings, axis=0)


def classify(text):

    text_vector = model.encode([text])[0]

    scores = {}

    for domain, proto in prototype_vectors.items():

        sim = cosine_similarity(
            [text_vector],
            [proto]
        )[0][0]

        scores[domain] = float(sim)

    return scores