import os
import re

def create_structure_from_text(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        lines = f.readlines()

    base_path = None
    stack = []

    for raw_line in lines:
        line = raw_line.rstrip("\n")

        # Skip empty or bracket-only lines
        if not line.strip() or line.strip() in ["[", "]"]:
            continue

        # Calculate indentation (4 spaces per level, flexible)
        indent_level = len(line) - len(line.lstrip(" "))

        # Clean out tree characters and weird Unicode lines
        clean_name = re.sub(r"[│├└─]+", "", line).strip()

        # Set base folder
        if base_path is None and clean_name.startswith("/") and clean_name.endswith("/"):
            base_path = clean_name.strip("/")
            os.makedirs(base_path, exist_ok=True)
            stack = [(indent_level, base_path)]
            continue

        # Adjust stack depth
        while stack and indent_level <= stack[-1][0]:
            stack.pop()

        parent_dir = stack[-1][1] if stack else "."

        # If it's a directory
        if clean_name.endswith("/"):
            dir_path = os.path.join(parent_dir, clean_name.strip("/"))
            os.makedirs(dir_path, exist_ok=True)
            stack.append((indent_level, dir_path))
        else:
            # It's a file
            file_path = os.path.join(parent_dir, clean_name)
            os.makedirs(os.path.dirname(file_path), exist_ok=True)
            with open(file_path, "w", encoding="utf-8") as f:
                pass  # create empty file

    print(f"✅ Done! Structure created at: {os.path.abspath(base_path)}")

if __name__ == "__main__":
    create_structure_from_text("data.txt")
