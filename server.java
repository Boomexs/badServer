import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.net.*;

interface Tree<T extends Comparable<T>> {
    void insert(T key);
    Node<T> search(T key);
    void delete(T key);
    String draw();
}

class Node<T extends Comparable<T>> {
    T key;
    Node<T> left, right;

    public Node(T item) {
        key = item;
        left = right = null;
    }
}

class BinarySearchTree<T extends Comparable<T>> implements Tree<T> {
    Node<T> root;

    BinarySearchTree() {
        root = null;
    }

    public void insert(T key) {
        root = insertRec(root, key);
    }

    Node<T> insertRec(Node<T> root, T key) {
        if (root == null) {
            root = new Node<>(key);
            return root;
        }

        if (key.compareTo(root.key) < 0)
            root.left = insertRec(root.left, key);
        else if (key.compareTo(root.key) > 0)
            root.right = insertRec(root.right, key);

        return root;
    }

    public Node<T> search(T key) {
        return searchRec(root, key);
    }

    Node<T> searchRec(Node<T> root, T key) {
        if (root == null || root.key.equals(key))
            return root;

        if (root.key.compareTo(key) > 0)
            return searchRec(root.left, key);

        return searchRec(root.right, key);
    }

    public void delete(T key) {
        root = deleteRec(root, key);
    }

    Node<T> deleteRec(Node<T> root, T key) {
        if (root == null)
            return root;

        if (key.compareTo(root.key) < 0)
            root.left = deleteRec(root.left, key);
        else if (key.compareTo(root.key) > 0)
            root.right = deleteRec(root.right, key);
        else {
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;

            root.key = minValue(root.right);
            root.right = deleteRec(root.right, root.key);
        }

        return root;
    }

    T minValue(Node<T> root) {
        T minv = root.key;
        while (root.left != null) {
            minv = root.left.key;
            root = root.left;
        }
        return minv;
    }

    public String draw() {
        StringBuilder toReturn = new StringBuilder();
        printTree(root, 0, toReturn);
        return toReturn.toString();
    }

    void printTree(Node<T> node, int level, StringBuilder appendTo) {
        if (node == null)
            return;
    
        printTree(node.right, level + 1, appendTo);
        if (level != 0) {
            for (int i = 0; i < level - 1; i++)
                appendTo.append("|\t");
            appendTo.append("|++").append(node.key).append("\n");
        } else {
            appendTo.append(node.key).append("\n");
        }
        printTree(node.left, level + 1, appendTo);
    }
}

public class server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4999);
        for (int i = 0; i < 8; i++) {
            try {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = serverSocket.accept();

                            System.out.println("Client connected");

                            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                            BufferedReader br = new BufferedReader(isr);
                            PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);


                            String command;
                            BinarySearchTree<?> tree;
                            command = br.readLine();

                            switch (command) {
                                case "int":
                                    tree = new BinarySearchTree<Integer>();
                                    ((BinarySearchTree<Integer>) tree).insert(0);
                                    pr.println("clear");
                                    pr.println(tree.draw());
                                    break;
                                case "str":
                                    tree = new BinarySearchTree<String>();
                                    ((BinarySearchTree<String>) tree).insert("root");
                                    pr.println("clear");
                                    pr.println(tree.draw());
                                    break;
                                case "double":
                                    tree = new BinarySearchTree<Double>();
                                    ((BinarySearchTree<Double>) tree).insert(0.0);
                                    pr.println("clear");
                                    pr.println(tree.draw());
                                    break;
                                default:
                                    tree = new BinarySearchTree<Integer>();
                                    ((BinarySearchTree<Integer>) tree).insert(0);
                                    pr.println("clear");
                                    pr.println(tree.draw());
                                    System.out.println("Making tree int as invalid type put in");
                                    pr.println("clear");
                                    pr.println("Make tree of type int next time try: int, str or double");
                                    break;
                            }
                            try {
                                while ((command = br.readLine()) != null) {
                                    System.out.println("client: " + command);
                                    try {
                                        String[] splited = command.split("\\s+");
                                        switch (splited[0]) {
                                            case "draw":
                                                pr.println("clear");
                                                pr.println(tree.draw());
                                                break;
                                            case "search":
                                                if (tree instanceof BinarySearchTree) {
                                                    pr.println("clear");
                                                    pr.println("search " + searchTree(tree, splited[1]));
                                                }
                                                break;
                                            case "insert":
                                                if (tree instanceof BinarySearchTree) {
                                                    insertTree(tree, splited[1]);
                                                    pr.println("clear");
                                                    pr.println(tree.draw());
                                                }
                                                break;
                                            case "delete":
                                                if (tree instanceof BinarySearchTree) {
                                                    deleteTree(tree, splited[1]);
                                                    pr.println("clear");
                                                    pr.println(tree.draw());
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                    } catch (Exception exception) {
                                        System.out.println("invalid command");
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("client connection");
                            }
                        }catch (IOException ioException){
                            System.out.println("connection problem");
                        }
                    }
                };
                thread.start();
            }catch (Exception e){
                System.out.println("could not create socket for client");
            }
        }
    }

        

    private static void insertTree(Tree<?> tree, String value) {
        if (tree instanceof BinarySearchTree) {
            if (((BinarySearchTree<?>) tree).root != null) {
                if (((BinarySearchTree<?>) tree).root.key instanceof Integer) {
                    ((BinarySearchTree<Integer>) tree).insert(Integer.parseInt(value));
                } else if (((BinarySearchTree<?>) tree).root.key instanceof String) {
                    ((BinarySearchTree<String>) tree).insert(value);
                } else if (((BinarySearchTree<?>) tree).root.key instanceof Double) {
                    ((BinarySearchTree<Double>) tree).insert(Double.parseDouble(value));
                }
            }
        }
    }

    private static void deleteTree(Tree<?> tree, String value) {
        if (tree instanceof BinarySearchTree) {
            if (((BinarySearchTree<?>) tree).root != null) {
                if (((BinarySearchTree<?>) tree).root.key instanceof Integer) {
                    ((BinarySearchTree<Integer>) tree).delete(Integer.parseInt(value));
                } else if (((BinarySearchTree<?>) tree).root.key instanceof String) {
                    ((BinarySearchTree<String>) tree).delete(value);
                } else if (((BinarySearchTree<?>) tree).root.key instanceof Double) {
                    ((BinarySearchTree<Double>) tree).delete(Double.parseDouble(value));
                }
            }
        }
    }

    private static boolean searchTree(Tree<?> tree, String value) {
        if (tree instanceof BinarySearchTree) {
            if (((BinarySearchTree<?>) tree).root != null) {
                if (((BinarySearchTree<?>) tree).root.key instanceof Integer) {
                    return ((BinarySearchTree<Integer>) tree).search(Integer.parseInt(value)).key != null;
                } else if (((BinarySearchTree<?>) tree).root.key instanceof String) {
                    return ((BinarySearchTree<String>) tree).search(value).key != null;
                } else if (((BinarySearchTree<?>) tree).root.key instanceof Double) {
                    return ((BinarySearchTree<Double>) tree).search(Double.parseDouble(value)).key != null;
                }
            }
        }
        return false;
    }
}