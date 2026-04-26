from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
from prototypes import DOMAIN_PROTOTYPES

model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")

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