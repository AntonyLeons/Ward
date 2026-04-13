import os, re

def process_file(filepath):
    if not os.path.exists(filepath): return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # URLs
    content = re.sub(r'th:href\s*=\s*\"@\{([^\}]+)\}\"', r'href="\1"', content)
    content = re.sub(r'th:src\s*=\s*\"@\{([^\}]+)\}\"', r'src="\1"', content)
    
    # Texts
    content = re.sub(r'th:text\s*=\s*\"\$\{([^\}]+)\}\"\s*>\s*</', r'>{{ \1 }}</', content)
    content = re.sub(r'th:text\s*=\s*\"\|\$\{([^\}]+)\}\s+\$\{([^\}]+)\}\|\"\s*>\s*</', r'>{{ \1 }}    {{ \2 }}</', content)

    # Theme attrs
    content = re.sub(r'th:attr\s*=\s*\"[^\"]+\"', r'theme="{{ theme }}" enableFog="{{ enable_fog }}" backgroundColor="{{ background_color }}"', content)
    
    # Inline JS
    content = re.sub(r'th:inline\s*=\s*\"javascript\"', '', content)

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

process_file('templates/index.html')
process_file('templates/setup.html')
process_file('templates/error.html')
