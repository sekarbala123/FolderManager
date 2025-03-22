# **FolderManager**  

## **Overview**  
**FolderManager** is a simple Java Swing-based GUI application that helps users manage folders efficiently. It allows users to add folders to a table, open them in **File Explorer**, delete them, and save/load the folder list for future use.  

## **Use Case**  
### **Problem Statement**  
Users often work with multiple folders and need an easy way to manage them. Manually opening, deleting, and tracking folders can be time-consuming.  

### **Solution**  
FolderManager provides an intuitive GUI that allows users to:  
- **Add Folders** – Select and add folders to a table.  
- **Open Folders** – Open a folder in **File Explorer** with a click.  
- **Delete Folders** – Remove folders from the list and delete them from the system.  
- **Save/Load Folder List** – Store and retrieve folders from a file for easy reuse.  

### **Example Scenario**  
1. The user launches **FolderManager**.  
2. Clicks **"Add Folder"** to select a folder and add it to the table.  
3. Clicks **"Open"** to open a folder in **File Explorer**.  
4. Clicks **"Delete"** to remove a folder (after confirmation).  
5. Clicks **"Save List"** to store the folder paths.  
6. Clicks **"Load List"** to restore the saved folder paths.  

## **Features**  
✔ **Graphical Interface** – Easy-to-use Swing-based GUI.  
✔ **Open Folders** – Quickly open folders in File Explorer.  
✔ **Delete Folders** – Permanently remove folders from the system.  
✔ **Save & Load List** – Persist folder lists for later use.  
✔ **Dynamic Table Management** – Real-time updates on folder operations.  

## **Technology Stack**  
- **Language:** Java  
- **GUI Framework:** Swing  
- **File Handling:** Java I/O  
- **Build Tool:** Maven (optional)  

## **Installation & Usage**  

### **1. Prerequisites**  
- Install Java 8+  
- (Optional) Install Eclipse/IntelliJ for development  

### **2. Clone the Repository**  
```sh
git clone https://github.com/your-username/FolderManager.git
cd FolderManager
```

### **3. Compile and Run**  
```sh
javac FolderManager.java
java FolderManager
```
or if using Maven:
```sh
mvn clean compile exec:java
```

## **Future Enhancements**  
- ✅ Support for multi-folder selection  
- ✅ Dark mode support  
- ✅ Drag-and-drop folder addition  

---

**📌 Author:** *Bala*  
🔗 **GitHub:** [sekarbala123](https://github.com/sekarbala123)
