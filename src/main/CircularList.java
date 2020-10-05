package main;

import java.util.ArrayList;

public class CircularList {

    private Node head;
    private Node tail;
    private Node lastUnconfirmed;
    private Node startPointer;

    private int sizeMax;
    private int counter;
    private int countInsertions;
    private int qntRead;
    private int totalMesages;
    ArrayList<String> received = new ArrayList<>();

    static class Node{

        private Object data;
        private Node next;
        private boolean read;
        private String key;
        private int age;

        Node(Object data, String key) {
            this.data = data;
            this.next = null;
            this.read = false;
            this.key = key;
            this.age = 0;
        }

        public Object getData(){
            return data;
        }

        public void setData(Object o) {
            this.data = o;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getNext() {
            return this.next;
        }

        public void setRead(boolean read) {
            this.read = read;
        }

        public boolean getRead() {
            return this.read;
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void incrementAge() {
            this.age++;
        }

        public int getAge() {
            return this.age;
        }

        public void resetAge() {
            this.age = 0;
        }
    }

    public CircularList(int size) {
        this.head = null;
        this.lastUnconfirmed = null;
        this.startPointer = null;
        this.sizeMax = size;
        this.counter = 0;
        this.countInsertions = 0;
        this.totalMesages = 0;
    }

    public void insert(Object data, String key) {
        //System.out.println("Entrou aqui pai. adicionando key: " + key);
        if (head == null) {
            head = new Node(data, key);
            head.setNext(head);
            tail = head;
            incrementCounter();
            incrementInsertions();
            return;
        }

        if (getCounter() == sizeMax) {
            insertBeforeRead(data, key);
            return;
        }

        Node tmp = new Node(data, key);

        tail.setNext(tmp);
        tmp.setNext(head);
        tail = tmp;

        incrementCounter();
        incrementInsertions();
    }

    public void insertBeforeRead(Object data, String key) {

        //System.out.println("Vamos procurar um lugar para + " + key);

        if (startPointer == null) {
            startPointer = head;
        }

        boolean replace = false;

        Node current = startPointer;

        while(true) {

            if (current.getRead()) {
                // Se current já foi lido.
                //System.out.println("Current já foi lido: " + current.getKey());
                replace = true;
                break;
            } else {
                // Avaliar a idade do nodo
                // Se for velho demais, reenviar e alterar.
                // resend current here.

                if (current.getAge() >= 1000000){
                    replace = true;
                    break;
                }

                current.incrementAge();

            }

            current = current.getNext();

        }

        if (replace) {
            current.setRead(false);
            current.setData(data);
            current.setKey(key);
            current.resetAge();
            incrementInsertions();
        }
        startPointer = current.getNext();
    }

    public String toString(){
        String output = "";

        if (head == null)
            return "** empity **";
        if (head.getNext() == null) {
            output += "-> [" + head.getKey() + "]";
        } else {
            Node current = head;
            while(current.getNext() != head) {
                output += concatString(current);
                current = current.getNext();
            }
            output += concatString(current);
        }
        return output;
    }

    public void markReadRecived() {
        if(lastUnconfirmed == null)
            lastUnconfirmed = head;
        if(head == null)
            return;

        synchronized (received) {

            ArrayList<String> checked = new ArrayList<>();

            for (String r : received) {

                Node current = lastUnconfirmed;
                //System.out.println("String procurada: " + r);
                //System.out.println("LastUnconfirmed: " + lastUnconfirmed.getKey());


                while (true) {

                    if(current.getKey().equals(r)) {
                        current.setRead(true);
                        checked.add(r);
                        incrementQntRead();
                        lastUnconfirmed = current.getNext();
                        break;
                    }

                    current = current.getNext();
                }
            }
            received.removeAll(checked);
        }
    }

    public String concatString(Node current) {
        return " -> [" + current.getKey() + " / " + current.getRead() + " " +
                current.getAge() + "]";
    }

    public void incrementCounter() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }

    public int getQntRead() {
        return qntRead;
    }

    public void setQntRead(int qntRead) {
        this.qntRead = qntRead;
    }

    public void incrementQntRead() {
        qntRead++;
    }

    public void incrementInsertions() {
        countInsertions++;
    }

    public int getCountInsertions() {
        return countInsertions;
    }

    public void setTotalMesages(int totalMesages) {
        this.totalMesages = totalMesages;
    }

    public int getTotalMesages() {
        return totalMesages;
    }

    public void addReceived(String r) {
        synchronized (received) {
            received.add(r);
        }
    }

    public int getSizeReceived() {
        synchronized (received) {
            return received.size();
        }
    }

    public void changeSize() {

        if (getTotalMesages() == 0)
            return;

        float percentRead = (float) getQntRead() / getTotalMesages();

        System.out.println("*** % De mensagens confirmadas: " + percentRead + "***");

        if (percentRead < 0.3) {
            sizeMax += sizeMax + (0.2 * sizeMax);
        }

        if (percentRead == 1) {
            System.out.println("Produção de mensagens encerrada.");
            setTotalMesages(0);
            setQntRead(0);
            setTotalMesages(0);
        }
    }
}