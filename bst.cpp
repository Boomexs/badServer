#include <iostream>
#include <iomanip>
#include <queue>
#include <memory>

class Node {
public:
    int key;
    std::shared_ptr<Node> left, right;

    Node(int k) : key(k), left(nullptr), right(nullptr) {}
};

class BinaryTree {
private:
    std::shared_ptr<Node> root;

    void insert(std::shared_ptr<Node>& node, int key) {
        if (!node) {
            node = std::make_shared<Node>(key);
        } else if (key < node->key) {
            insert(node->left, key);
        } else {
            insert(node->right, key);
        }
    }

    std::shared_ptr<Node> search(std::shared_ptr<Node> node, int key) {
        if (!node || node->key == key) {
            return node;
        } else if (key < node->key) {
            return search(node->left, key);
        } else {
            return search(node->right, key);
        }
    }

    std::shared_ptr<Node> deleteNode(std::shared_ptr<Node> root, int key) {
        if (!root) return root;

        if (key < root->key) {
            root->left = deleteNode(root->left, key);
        } else if (key > root->key) {
            root->right = deleteNode(root->right, key);
        } else {
            if (!root->left) {
                return root->right;
            } else if (!root->right) {
                return root->left;
            }

            std::shared_ptr<Node> temp = minValueNode(root->right);
            root->key = temp->key;
            root->right = deleteNode(root->right, temp->key);
        }
        return root;
    }

    std::shared_ptr<Node> minValueNode(std::shared_ptr<Node> node) {
        std::shared_ptr<Node> current = node;
        while (current && current->left) {
            current = current->left;
        }
        return current;
    }

    void draw(std::shared_ptr<Node> root, int space = 0, int height = 10) {
        if (!root) return;
        space += height;
        draw(root->right, space);
        std::cout << std::endl;
        for (int i = height; i < space; i++) std::cout << ' ';
        std::cout << root->key << "\n";
        draw(root->left, space);
    }

public:
    BinaryTree() : root(nullptr) {}

    void insert(int key) {
        insert(root, key);
    }

    bool search(int key) {
        return search(root, key) != nullptr;
    }

    void deleteNode(int key) {
        root = deleteNode(root, key);
    }

    void draw() {
        draw(root);
    }
};

int main() {
    BinaryTree tree;
    tree.insert(50);
    tree.insert(30);
    tree.insert(20);
    tree.insert(40);
    tree.insert(70);
    tree.insert(60);
    tree.insert(80);

    std::cout << "Drzewo binarne:\n";
    tree.draw();

    std::cout << "\nUsuwanie 20\n";
    tree.deleteNode(20);
    tree.draw();

    std::cout << "\nUsuwanie 30\n";
    tree.deleteNode(30);
    tree.draw();

    std::cout << "\nUsuwanie 50\n";
    tree.deleteNode(50);
    tree.draw();

    return 0;
}