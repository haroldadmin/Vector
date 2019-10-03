import os, subprocess, shutil
from pathlib import Path

current_dir = Path(os.path.dirname(os.path.realpath(__file__)))
api_ref_dir = current_dir.joinpath("docs/api/")
gradlew_path = current_dir.joinpath("gradlew")

def delete_existing_api_ref():
  print(f"API Reference directory: {api_ref_dir}")
  for path in api_ref_dir.glob("**/*"):
    print(f"Deleting {path}")
    if path.is_file():
        path.unlink()
    elif path.is_dir():
        shutil.rmtree(path)
  
def generate_api_ref():
  print(f"Gradle wrapper at {gradlew_path}")
  print("Generating KDocs using Dokka...")
  subprocess.call([gradlew_path, ":vector:dokka"])

if __name__ == "__main__":
  delete_existing_api_ref()
  generate_api_ref()