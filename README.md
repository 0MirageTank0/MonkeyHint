# Monkey Hint - Custom Code Hints for PyCharm
![image](https://github.com/user-attachments/assets/9a28e5dd-6660-4deb-a2c2-f3d7927b05f6)

**A PyCharm plugin that allows users to add custom IDE code hints for specific symbols.**

**一个PyCharm插件，允许用户对特殊符号添加自定义的IDE代码补全提示**

---

## Introduction

This plugin lets you extend and personalize code completions in PyCharm without modifying your existing codebase or project files.

## Features

- 🎯 **Custom Code Hints**: Add tailored code hints for any symbol in your project.
- 🔒 **Non-intrusive**: No modification of your existing code or project files required.
- ⚠️ **Hardcoded Trigger**: Ignores namespace and scope checks. Code hints will trigger even if the symbol is undefined (perhaps not exactly a "feature").

## Usage

1. Enter the desired code hints in the sidebar window:
![image](https://github.com/user-attachments/assets/a1181b55-3da8-40ad-9c98-b07b21db6e08)

The format resembles Python syntax:

```python
symbol.attribute_name:attribute_type #comment
#or:
symbol.attribute_name
```
The comment will appear after the code hint in gray text. 
The attribute type doesn’t need to follow Python conventions, so you can write anything:
```python
custom.english_hint:English # English Hint
custom.chinese_hint:中文 #中文提示
custom.japanese_hint:日本語 #日本語のヒント
```
![image](https://github.com/user-attachments/assets/f54920ae-be26-43ca-b369-587b721ffd8f)

---

This plugin may be useful if:
- Your project includes a global singleton.
- .pyi files are not feasible.
- The properties you need have a global lifecycle.
- You primarily need easy access to property names for efficient coding.


## Why This Plugin?

This plugin is designed for projects with unique requirements where conventional code completion may fall short.

For example, in Blender plugin development, the environment includes a singleton scene object that persists throughout the plugin's lifecycle.

To enable persistent storage of custom variables, developers often add custom properties directly to scene.

As the project grows and spans multiple submodules, managing and recalling these property names can become challenging 

(while it's possible to define custom structures for static code hints, you still need to remember each property’s name within the singleton).

Creating a .pyi file for a large object like scene is impractical due to its complexity.


