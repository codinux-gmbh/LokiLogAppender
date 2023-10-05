package net.codinux.log.loki.model

open class OpenArrayList<E>(private val impl: MutableList<E>) : MutableList<E> {

    constructor() : this(ArrayList())

    constructor(initialCapacity: Int) : this(ArrayList(initialCapacity))

    constructor(elements: Collection<E>) : this(ArrayList(elements))

    constructor(vararg elements: E): this(elements.asList())


    override val size: Int
        get() = impl.size

    override fun isEmpty() = impl.isEmpty()

    override fun iterator() = impl.iterator()

    override fun listIterator() = impl.listIterator()

    override fun listIterator(index: Int) = impl.listIterator(index)

    override fun indexOf(element: E) = impl.indexOf(element)

    override fun lastIndexOf(element: E) = impl.lastIndexOf(element)

    override fun subList(fromIndex: Int, toIndex: Int) = impl.subList(fromIndex, toIndex)

    override fun contains(element: E) = impl.contains(element)

    override fun containsAll(elements: Collection<E>) = impl.containsAll(elements)

    override fun get(index: Int) = impl.get(index)

    override fun add(element: E) = impl.add(element)

    override fun add(index: Int, element: E) = impl.add(index, element)

    override fun set(index: Int, element: E) = impl.set(index, element)

    override fun remove(element: E) = impl.remove(element)

    override fun removeAt(index: Int) = impl.removeAt(index)

    override fun addAll(elements: Collection<E>) = impl.addAll(elements)

    override fun addAll(index: Int, elements: Collection<E>) = impl.addAll(index, elements)

    override fun removeAll(elements: Collection<E>) = impl.removeAll(elements)

    override fun retainAll(elements: Collection<E>) = impl.retainAll(elements)

    override fun clear() = impl.clear()

    override fun toString() = impl.toString()

}