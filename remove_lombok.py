#!/usr/bin/env python3
"""
Script to remove Lombok annotations and add manual getters/setters
"""
import os
import re
from pathlib import Path

def generate_getters_setters(class_name, fields):
    """Generate getter and setter methods for fields"""
    methods = []
    for field_type, field_name in fields:
        # Capitalize first letter for method name
        capitalized = field_name[0].upper() + field_name[1:] if len(field_name) > 1 else field_name.upper()
        
        # Getter
        if field_type.lower() == 'boolean':
            getter_prefix = 'is'
        else:
            getter_prefix = 'get'
        
        getter = f"""
    public {field_type} {getter_prefix}{capitalized}() {{
        return {field_name};
    }}"""
        
        # Setter
        setter = f"""
    public void set{capitalized}({field_type} {field_name}) {{
        this.{field_name} = {field_name};
    }}"""
        
        methods.append(getter)
        methods.append(setter)
    
    return '\n'.join(methods)

def remove_lombok_from_file(file_path):
    """Remove Lombok annotations and add getters/setters"""
    with open(file_path, 'r') as f:
        content = f.read()
    
    # Remove Lombok imports
    content = re.sub(r'import lombok\..*;\n', '', content)
    
    # Remove @Data, @NoArgsConstructor, @AllArgsConstructor annotations
    content = re.sub(r'@Data\n', '', content)
    content = re.sub(r'@NoArgsConstructor\n', '', content)
    content = re.sub(r'@AllArgsConstructor\n', '', content)
    content = re.sub(r'@Getter\n', '', content)
    content = re.sub(r'@Setter\n', '', content)
    
    # Extract fields (simplified pattern)
    field_pattern = r'^\s*(?:@\w+(?:\([^)]*\))?\s*)*\s*private\s+(\S+(?:<[^>]+>)?)\s+(\w+);'
    fields = re.findall(field_pattern, content, re.MULTILINE)
    
    # Get class name
    class_match = re.search(r'public class (\w+)', content)
    if not class_match:
        print(f"Warning: Could not find class name in {file_path}")
        return False
    
    class_name = class_match.group(1)
    
    # Generate getters and setters
    if fields:
        getters_setters = generate_getters_setters(class_name, fields)
        
        # Find the last closing brace of the class
        last_brace_pos = content.rfind('}')
        if last_brace_pos != -1:
            # Insert getters/setters before the last closing brace
            content = content[:last_brace_pos] + getters_setters + '\n' + content[last_brace_pos:]
    
    # Add default constructor
    # Find position after class declaration
    class_body_start = content.find('{', content.find(f'public class {class_name}'))
    if class_body_start != -1:
        # Check if there are existing constructors
        if not re.search(rf'public {class_name}\s*\(', content):
            constructor = f"""
    
    public {class_name}() {{
    }}"""
            # Find first field or end of class
            first_field_match = re.search(r'^\s*(?:@\w+(?:\([^)]*\))?\s*)*\s*private\s+', content[class_body_start:], re.MULTILINE)
            if first_field_match:
                insert_pos = class_body_start + first_field_match.start()
                content = content[:insert_pos] + constructor + '\n' + content[insert_pos:]
    
    # Write back
    with open(file_path, 'w') as f:
        f.write(content)
    
    return True

def main():
    backend_path = Path(__file__).parent
    
    # Find all Java files in entity and dto directories
    entity_files = list((backend_path / 'src/main/java/com/qpmanagement/entity').glob('*.java'))
    dto_files = list((backend_path / 'src/main/java/com/qpmanagement/dto').glob('*.java'))
    
    all_files = entity_files + dto_files
    
    print(f"Processing {len(all_files)} files...")
    
    for file_path in all_files:
        print(f"Processing: {file_path.name}")
        remove_lombok_from_file(file_path)
    
    print("Done! Lombok annotations removed and getters/setters added.")

if __name__ == '__main__':
    main()
