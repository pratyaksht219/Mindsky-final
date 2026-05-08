import json
import argparse
from pathlib import Path
from classifier import classify
from sklearn.metrics import classification_report, accuracy_score

def load_data(file_path):
    path = Path(file_path)
    if not path.exists():
        raise FileNotFoundError(f"Dataset not found at {file_path}")
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)

def evaluate(dataset_path):
    data = load_data(dataset_path)
    
    if not data:
        print("Dataset is empty.")
        return

    print(f"Loaded {len(data)} test examples from {dataset_path}.")
    print("Running classification (this might take a moment if the model is loading...)")
    
    y_true = []
    y_pred = []
    failed_examples = []

    for item in data:
        text = item.get("text")
        true_label = item.get("label")
        
        if not text or not true_label:
            print(f"Skipping invalid item: {item}")
            continue

        # Get scores from the service
        scores = classify(text)
        
        # Get the highest scoring domain (Top-1 Prediction)
        predicted_domain = max(scores, key=scores.get)
        
        y_true.append(true_label)
        y_pred.append(predicted_domain)
        
        if predicted_domain != true_label:
            failed_examples.append({
                "text": text,
                "expected": true_label,
                "predicted": predicted_domain,
                "score": scores[predicted_domain]
            })

    print("\n" + "="*50)
    print("EVALUATION RESULTS")
    print("="*50)
    
    acc = accuracy_score(y_true, y_pred)
    print(f"\nOverall Accuracy: {acc * 100:.2f}%\n")
    
    print("Classification Report:")
    # zero_division=0 prevents warnings if a class isn't predicted
    print(classification_report(y_true, y_pred, zero_division=0))
    
    if failed_examples:
        print("\n" + "="*50)
        print("MISCLASSIFIED EXAMPLES (For Debugging & Tuning)")
        print("="*50)
        print("Use these to improve the sentences in prototypes.py\n")
        for i, fail in enumerate(failed_examples, 1):
            print(f"{i}. Text: '{fail['text']}'")
            print(f"   Expected: {fail['expected']} | Predicted: {fail['predicted']} (Confidence: {fail['score']:.2f})")
            print("-" * 30)
    else:
        print("\nNo misclassified examples! The model scored 100%.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Evaluate ML Screening Engine Classifier Accuracy")
    parser.add_argument("--data", type=str, default="eval_data.json", help="Path to evaluation JSON dataset")
    args = parser.parse_args()
    
    evaluate(args.data)
