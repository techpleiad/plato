export class DoublyLinkedList<T> {
    head: Node<T> | any = null;
    tail: Node<T> | any = null;
  
    appendLast(data: T): void {
      const node = new Node(data);
  
      if (!this.head) {
        this.head = node;
      }
      else {
        this.tail.next = node;
        node.prev = this.tail;
      }
      this.tail = node;
    }
  
    delete(node: Node<T>): void {
      if (!node.prev) { // head node
        this.head = node.next; // move head to next node
        if (this.head) {       // if next node present, prev to null
          this.head.prev = null;
        }
      }
      else if (!node.next) { // tail node
        this.tail = node.prev;
        if (this.tail) {
          if (this.tail) {
            this.tail.next = null;
          }
        }
      }
      else { // middle node
        node.prev.next = node.next;
        node.next.prev = node.prev;
      }
      node.destroy();
      // console.log(node);
    }
  
    printList(): void {
      let curr: Node<T> = this.head;
      while (curr) {
        console.log(curr.data);
        curr = curr.next;
      }
    }
  }
  export class Node<T> {
    public next: Node<T> | any = null;
    public prev: Node<T> | any = null;
    constructor(public data: T) {}
  
    destroy(): void {
      this.next = null;
      this.prev = null;
    }
  }
  