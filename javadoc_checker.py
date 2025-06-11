#!/usr/bin/env python3
"""
Script to find Java classes and methods missing Javadoc documentation.
"""

import os
import re
import sys
from pathlib import Path

def has_class_javadoc(content, class_line_num):
    """Check if a class/interface/enum has Javadoc documentation."""
    lines = content.split('\n')
    
    # Look backwards from the class declaration for Javadoc
    for i in range(class_line_num - 1, -1, -1):
        line = lines[i].strip()
        
        # If we hit another declaration or import, stop looking
        if (line.startswith('import ') or 
            line.startswith('package ') or
            re.match(r'^\s*(public|private|protected)?\s*(class|interface|enum|abstract)', line)):
            if i != class_line_num:  # Don't count the current class line
                break
        
        # Found end of Javadoc comment
        if line.endswith('*/'):
            # Look backwards to find start of Javadoc
            for j in range(i, -1, -1):
                if lines[j].strip().startswith('/**'):
                    return True
            break
        
        # If we hit any non-whitespace, non-comment line, no Javadoc
        if line and not line.startswith('*') and not line.startswith('//') and line != '*/':
            break
    
    return False

def has_method_javadoc(content, method_line_num):
    """Check if a method has Javadoc documentation."""
    lines = content.split('\n')
    
    # Look backwards from the method declaration for Javadoc
    for i in range(method_line_num - 1, -1, -1):
        line = lines[i].strip()
        
        # Skip annotations and modifiers
        if (line.startswith('@') or 
            line in ['public', 'private', 'protected', 'static', 'final', 'abstract', 'synchronized']):
            continue
        
        # Found end of Javadoc comment
        if line.endswith('*/'):
            # Look backwards to find start of Javadoc
            for j in range(i, -1, -1):
                if lines[j].strip().startswith('/**'):
                    return True
            break
        
        # If we hit any non-whitespace, non-comment line, no Javadoc
        if line and not line.startswith('*') and not line.startswith('//') and line != '*/':
            break
    
    return False

def analyze_java_file(file_path):
    """Analyze a Java file for missing Javadoc."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        return {"error": str(e)}
    
    lines = content.split('\n')
    missing_docs = []
    
    # Find class/interface/enum declarations
    class_pattern = re.compile(r'^\s*(public|private|protected)?\s*(abstract\s+)?(class|interface|enum)\s+(\w+)')
    method_pattern = re.compile(r'^\s*(public|protected)\s+.*?\s+(\w+)\s*\([^)]*\)\s*(\{|throws)')
    
    for i, line in enumerate(lines):
        # Check for class/interface/enum declarations
        class_match = class_pattern.search(line)
        if class_match:
            class_type = class_match.group(3)
            class_name = class_match.group(4)
            if not has_class_javadoc(content, i):
                missing_docs.append({
                    'type': 'class',
                    'name': f"{class_type} {class_name}",
                    'line': i + 1
                })
        
        # Check for public method declarations (but not constructors or overrides)
        method_match = method_pattern.search(line)
        if method_match:
            method_name = method_match.group(2)
            # Skip constructors (method name same as class name)
            # Skip if it's an override (check previous lines for @Override)
            has_override = False
            for j in range(max(0, i-5), i):
                if '@Override' in lines[j]:
                    has_override = True
                    break
            
            if not has_override and not has_method_javadoc(content, i):
                missing_docs.append({
                    'type': 'method',
                    'name': method_name,
                    'line': i + 1
                })
    
    return missing_docs

def main():
    scanner_core_src = Path("/home/ic0ns/Projects/claude-playground/Scanner-Core/src/main/java")
    
    if not scanner_core_src.exists():
        print("Scanner-Core source directory not found!")
        return
    
    undocumented_classes = []
    undocumented_methods = []
    
    # Find all Java files
    java_files = list(scanner_core_src.rglob("*.java"))
    
    print(f"Analyzing {len(java_files)} Java files...")
    print("=" * 60)
    
    for java_file in java_files:
        rel_path = java_file.relative_to(scanner_core_src.parent.parent.parent)
        result = analyze_java_file(java_file)
        
        if isinstance(result, dict) and "error" in result:
            print(f"ERROR analyzing {rel_path}: {result['error']}")
            continue
        
        file_classes = [item for item in result if item['type'] == 'class']
        file_methods = [item for item in result if item['type'] == 'method']
        
        if file_classes:
            undocumented_classes.append({
                'file': str(java_file),
                'rel_path': str(rel_path),
                'missing': file_classes
            })
        
        if file_methods:
            undocumented_methods.append({
                'file': str(java_file),
                'rel_path': str(rel_path),
                'missing': file_methods
            })
    
    # Print results
    print("\n" + "=" * 60)
    print("CLASSES/INTERFACES/ENUMS MISSING JAVADOC:")
    print("=" * 60)
    
    if not undocumented_classes:
        print("✓ All classes have Javadoc documentation!")
    else:
        for file_info in undocumented_classes:
            print(f"\nFile: {file_info['rel_path']}")
            for missing in file_info['missing']:
                print(f"  - Line {missing['line']}: {missing['name']}")
    
    print(f"\nTotal classes missing documentation: {sum(len(f['missing']) for f in undocumented_classes)}")
    
    print("\n" + "=" * 60)
    print("PUBLIC METHODS MISSING JAVADOC:")
    print("=" * 60)
    
    if not undocumented_methods:
        print("✓ All public methods have Javadoc documentation!")
    else:
        for file_info in undocumented_methods:
            print(f"\nFile: {file_info['rel_path']}")
            for missing in file_info['missing']:
                print(f"  - Line {missing['line']}: {missing['name']}")
    
    print(f"\nTotal methods missing documentation: {sum(len(f['missing']) for f in undocumented_methods)}")
    
    # Summary
    total_missing_classes = sum(len(f['missing']) for f in undocumented_classes)
    total_missing_methods = sum(len(f['missing']) for f in undocumented_methods)
    
    print("\n" + "=" * 60)
    print("SUMMARY:")
    print("=" * 60)
    print(f"Files analyzed: {len(java_files)}")
    print(f"Classes missing Javadoc: {total_missing_classes}")
    print(f"Methods missing Javadoc: {total_missing_methods}")
    print(f"Files with missing class docs: {len(undocumented_classes)}")
    print(f"Files with missing method docs: {len(undocumented_methods)}")

if __name__ == "__main__":
    main()