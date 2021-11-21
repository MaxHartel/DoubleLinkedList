import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Double-linked node implementation of IndexedUnsortedList
 * @author maxhartel
 *
 * @param <T> Generic type
 */
public class IUDoubleLinkedList<T> implements IndexedUnsortedList<T> {
	private Node<T> head;
	private Node<T> tail;
	private int size;
	private int modCount;
	
	/**
	 * Initialize a new empty list
	 */
	public IUDoubleLinkedList() {
		head = null;
		tail = null;
		size = 0;
		modCount = 0;
	}

	@Override
	public void addToFront(T element) {
		Node<T> newNode = new Node<T>(element);
		newNode.setNext(head);
		
		if (isEmpty()) {//special case: empty list
			tail = newNode;
		}else {//general case
			head.setPrevious(newNode);
		}
		
		head = newNode;
		size++;
		modCount++;		
	}

	@Override
	public void addToRear(T element) {
		Node<T> newNode = new Node<T>(element);
		newNode.setPrevious(tail);
		
		if(isEmpty()) {//special case: empty list
			head = newNode;
		}else {// general case
			tail.setNext(newNode);
		}
		
		tail = newNode;
		size++;
		modCount++;
	}

	@Override
	public void add(T element) {
		Node<T> newNode = new Node<T>(element);
		newNode.setPrevious(tail);
		
		if(isEmpty()) {//special case: empty list
			head = newNode;
		}else {// general case
			tail.setNext(newNode);
		}
		
		tail = newNode;
		size++;
		modCount++;
		
	}

	@Override
	public void addAfter(T element, T target) {
	Node<T> targetNode = head;
	
	if(targetNode == null) {//confirms that chosen element is a element
		throw new NoSuchElementException();
	}
	
	if (!contains(target)) {//confirms that chosen element is in list
		throw new NoSuchElementException();
		
	}
	
	while (targetNode != null && !targetNode.getElement().equals(target)) {// sets targetNode to the node that contains the target element
		targetNode = targetNode.getNext();
	}
	
	//adds a new node after targetNode
	Node<T> newNode = new Node<T>(element);
	newNode.setNext(targetNode.getNext());
	newNode.setPrevious(targetNode);
	targetNode.setNext(newNode);
	
	if(targetNode == tail) {
		tail = newNode;
	}else {
		newNode.getNext().setPrevious(newNode);
	}
	
	size++;
	modCount++;
	
	}

	@Override
	public void add(int index, T element) {
		if (index < 0 || index > size) {//confirms index is in the valid range
			throw new IndexOutOfBoundsException();
			
		} else if (index == 0) { //special case: adding to front
			addToFront(element);
			
		} else if (index == size) {//special case: adding to rear
			addToRear(element);
		}
		
		else {//general case
			Node<T> current = head;
			Node<T> elementNode = new Node<T>(element);
			
			//iterates through list to set "current" to the node at the correct index
			for (int i = 0; i < index; i++) {
				current = current.getNext();
			}
			
			//links the new node to the node at the specified index, and before the specified index
			elementNode.setNext(current);
			elementNode.setPrevious(current.getPrevious());
			
			current.getPrevious().setNext(elementNode);
			current.setPrevious(elementNode);
	
			size++;
			modCount++;
		}
	}

	@Override
	public T removeFirst() {
		if (isEmpty()) {//confirms that there are elements in the list to remove
			throw new NoSuchElementException();
		}
		T retVal = head.getElement();

		//special case: one element list
		if (head == tail) {
			head = null;
			tail = null;
			
		} else {//general case: removes first element from multi-element list
			Node<T> newHead = head.getNext();
			newHead.setPrevious(null);
			head = newHead;
		}

		modCount++;
		size--;
		return retVal;
	}

	@Override
	public T removeLast() {
		if (isEmpty()) {//confirms that there are elements in the list to remove
			throw new NoSuchElementException();
		}
		T retVal = tail.getElement();

		//special case: one element list
		if (head == tail) {
			head = null;
			tail = null;
			
		} else {//general case: removes last element from multi-element list
			Node<T> newTail = tail.getPrevious();
			newTail.setNext(null);
			tail = newTail;
		}

		modCount++;
		size--;
		return retVal;
	}

	@Override
	public T remove(T element) {
		
		if (!contains(element)) {//confirms that chosen element is in list
			throw new NoSuchElementException();
		}
		
		Node<T> current = head;
		
		//special case: first element removal
		if(current.getElement().equals(element)) {
			return removeFirst();
		}
		
		//iterates through list to set "current" to the node before the correct element
		while (!current.getNext().getElement().equals(element)) {
			current = current.getNext();
		}
		
		//special case last element  removal
		if(current.getNext() == tail) {
			return removeLast();
		}
		
		//somewhere in the middle (general  case)
		//links the nodes before and after the target element to each other instead of the target
		T retVal = current.getNext().getElement();
		
		Node<T> afterDeleted = current.getNext().getNext();
		
		current.setNext(current.getNext().getNext());
		afterDeleted.setPrevious(current);

		size--;
		modCount++;

		return retVal;
	}


	@Override
	public T remove(int index) {
		T retVal;

		if (index < 0 || index >= size) {//confirms index is in the valid range
			throw new IndexOutOfBoundsException();
			
		} else if (index == 0) {//special case first element  removal
			return removeFirst();
			
		} else if (index == size-1) {//special case last element  removal
			return removeLast();
			
		} else {
			//iterates through list to set "current" to the node before the chosen index
			Node<T> current = head;
			int IndexofInterest = index - 1;

			for (int i = 0; i < IndexofInterest; i++) {
				current = current.getNext();
			}
			
			//special case last element  removal pt2
			if(current.getNext() == tail) {
				tail = current;
			}
			
			//special case first element  removal pt2
			if(head.getNext() == current) {
				head = current;
			}
			
			//links the nodes before and after the target element to each other instead of the target
			retVal = current.getNext().getElement();
			Node<T> afterDeleted = current.getNext().getNext();
			current.setNext(afterDeleted);
			afterDeleted.setPrevious(current);
			
			
			size--;
			modCount++;
		}

		return retVal;
	}

	@Override
	public void set(int index, T element) {

		if (index < 0 || index >= size) {//confirms index is in the valid range
			throw new IndexOutOfBoundsException();
			
		} else if (index == 0) {//Special case: set first element
			head.setElement(element);
			
		} else if (index == size-1) {//Special case: set last element
			tail.setElement(element);
			
		} else {
			
			Node<T> current = head;
			
			for (int i = 0; i < index; i++) {//iterates through list to set "current" to the node at the correct index
				current = current.getNext();
			}
			current.setElement(element);
			
		}
		modCount++;

	}

	@Override
	public T get(int index) {
		if (index < 0 || index >= size) {//confirms index is in the valid range
			throw new IndexOutOfBoundsException();
		}

		Node<T> current = head;
		
		for (int i = 0; i < index; i++) {//iterates through list to set "current" to the node at the correct index
			current = current.getNext();
		}
		
		return current.getElement();
	}

	@Override
	public int indexOf(T element) {
		Node<T> current = head;
		int currentIndex = 0;

		//iterates through list to set "currentIndex" to the node at the correct index
		while (current != null && !element.equals(current.getElement())) {
			current = current.getNext();
			currentIndex++;
		}
		if (current == null) {//sets currentIndex to -1 if element is not found in list
			currentIndex = -1;
		}
		return currentIndex;
	}

	@Override
	public T first() {
		if(isEmpty()) {//checks if list has elements
			throw new NoSuchElementException();
		}
		return head.getElement();
	}

	@Override
	public T last() {
		if(isEmpty()) {//checks if list has elements
			throw new NoSuchElementException();
		}
		return tail.getElement();
	}

	@Override
	public boolean contains(T target) {
		if(indexOf(target) > -1) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public boolean isEmpty() {
		if(size == 0) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public int size() {
		return size;
	}
	
	@Override
	public String toString() {
		StringBuilder retString = new StringBuilder();

		retString.append("[");

		Iterator<T> it = this.iterator();
		
		while (it.hasNext()) {//adds a comma in between every list element
			retString.append(it.next());
			retString.append(", ");
		}
		
		if (!isEmpty()) {//confirms if there are elements in the list
			retString.delete(retString.length() - 2, retString.length());
		}

		retString.append("]");

		return retString.toString();
	}

	@Override
	public Iterator<T> iterator() {
		return new DLLIterator();
	}

	@Override
	public ListIterator<T> listIterator() {
		return new DLLIterator();
	}

	@Override
	public ListIterator<T> listIterator(int startingIndex) {
		return new DLLIterator(startingIndex);
	}
	
	/**
	 * 
	 * ListIterator for DoubleLinkedList - also serves as Basic Iterator
	 *
	 */
	private class DLLIterator implements ListIterator<T>{
		private Node<T> nextNode;
		private int nextIndex;
		private Node<T> lastReturned;
		private int iterModCount;
		
		/**Initialize iterator in front of first element*/
		public DLLIterator() {
			this(0);
			nextNode = head;
			nextIndex = 0;
			iterModCount = modCount;
		}
		
		/** Initialize iterator in front of element at startingIndex.
		 * @param startingIndex index of element that would be next
		 */
		public DLLIterator(int startingIndex) {
			if(startingIndex < 0 || startingIndex > size) {
				throw new IndexOutOfBoundsException();
			}
			
			nextNode = head;
			
			for(int i = 0; i < startingIndex; i++) {//advances nextNode to startingIndex
				nextNode = nextNode.getNext();
			}
			
			nextIndex = startingIndex;
				iterModCount = modCount;
		}

		@Override
		public boolean hasNext() {
			if(iterModCount != modCount) {//checks for listIterator concurrency
				throw new ConcurrentModificationException();
			}
			
			if(nextNode != null) {
				return true;
			}else {
				return false;
			}
		}

		@Override
		public T next() {
			if(iterModCount != modCount) {//checks for listIterator concurrency
				throw new ConcurrentModificationException();
			}
			
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			
			if(nextNode == null) {//special case: iterator is at the end of the list
				nextNode = tail;
			}
			
			T retVal = nextNode.getElement();
			lastReturned = nextNode;
			nextNode = nextNode.getNext();
			nextIndex++;
			
			return retVal;
		}

		@Override
		public boolean hasPrevious() {
			if(iterModCount != modCount) {//checks for listIterator concurrency
				throw new ConcurrentModificationException();
			}
			
			if(nextNode != head) {
				return true;
			}else {
				return false;
			}
		}

		@Override
		public T previous() {
			if(iterModCount != modCount) {//checks for listIterator concurrency
				throw new ConcurrentModificationException();
			}
			
			if(!hasPrevious()) {
				throw new NoSuchElementException();
			}
			
			if(nextNode == null) {//special case: iterator is at the end of the list
				nextNode = tail;
				
			}else{//general case
				nextNode = nextNode.getPrevious();
			}
			
			nextIndex--;
			lastReturned = nextNode;	
			
			return nextNode.getElement();
		}

		@Override
		public int nextIndex() {
			if(iterModCount != modCount) {//checks for listIterator concurrency
				throw new ConcurrentModificationException();
			}
			
			return nextIndex;
		}

		@Override
		public int previousIndex() {
			if(iterModCount != modCount) {//checks for listIterator concurrency
				throw new ConcurrentModificationException();
			}
			
			return (nextIndex - 1);
		}

		@Override
		public void remove() {
			if(iterModCount != modCount) {//checks for listIterator concurrency
				throw new ConcurrentModificationException();
			}
			
			if(lastReturned == null) {// checks if there was a call to previous or next
				throw new IllegalStateException();
			}
			
			if(lastReturned != tail) {//general case 1: removing element not from rear
				lastReturned.getNext().setPrevious(lastReturned.getPrevious());	
				
				}else {//special case: removing last element
					tail = lastReturned.getPrevious();
				}
			
			if(lastReturned != head) {//general case 2: removing element not from head
				lastReturned.getPrevious().setNext(lastReturned.getNext());	
				
				}else {//special case: removing first element
					head = lastReturned.getNext();
				}
			
			if(lastReturned == nextNode) {//last move was previous
				nextNode = nextNode.getNext();
				
			}else {//last move was next
				nextIndex--;
			}
			
			size--;
			modCount++;
			iterModCount++;
			lastReturned = null;		
		}

		@Override
		public void set(T e) {
			if(iterModCount != modCount) {//checks for listIterator concurrency
				throw new ConcurrentModificationException();
			}
			
			if(lastReturned == null) {// checks if there was a call to previous or next
				throw new IllegalStateException();
			}
			
			lastReturned.setElement(e);
			
			modCount++;
			iterModCount++;
		}

		@Override
		public void add(T e) {
			if(iterModCount != modCount) {//checks for listIterator concurrency
				throw new ConcurrentModificationException();
			}
			
			Node<T> newNode = new Node<T>(e);
			newNode.setNext(nextNode);
			
			if(nextNode != null) {//if not adding to the end of the list, makes sure the next node is linked correctly
				newNode.setPrevious(nextNode.getPrevious());
				nextNode.setPrevious(newNode);
				
			}else if(nextNode == null){//// if adding to the end of the list, resets tail to new node
				newNode.setPrevious(tail);
				tail = newNode;
			}
			
			if(nextNode != head) {// if not adding to front of list, makes sure the previous node is linked correctly
				newNode.getPrevious().setNext(newNode);
				
			}else {// if adding to front of list, resets head to new Node
				head = newNode;
			}
			
			size++;
			modCount++;
			iterModCount++;
			nextIndex++;
			lastReturned = null;
		}
		
	}

}










