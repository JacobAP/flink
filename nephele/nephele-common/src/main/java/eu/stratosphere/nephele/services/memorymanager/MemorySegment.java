/***********************************************************************************************************************
 *
 * Copyright (C) 2010 by the Stratosphere project (http://stratosphere.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/

package eu.stratosphere.nephele.services.memorymanager;


import java.nio.ByteBuffer;


/**
 * This class represents a piece of memory allocated from the memory manager.
 * The memory segment comes with multiple views, that can be used to put data
 * into it, or get data from it:
 * <ul>
 *   <li>A {@link eu.stratosphere.nephele.services.memorymanager.DataOutputView}, which can be used to write data to
 *       the segment much like to a stream, via the {@link java.io.DataOutput} interface.</li>
 *   <li>A {@link eu.stratosphere.nephele.services.memorymanager.DataInputView}, which can be used to read data
 *       from the segment much like from a stream, via the {@link java.io.DataInput} interface.</li>
 *   <li>A {@link eu.stratosphere.nephele.services.memorymanager.RandomAccessView}, which can be used to put data at random
 *       locations into the segment.</li>
 * </ul>
 * All the view operate independent from each other. You read more data from the segment through the input view than
 * you wrote before. In that case, the contents is undefined.
 *
 * @author Alexander Alexandrov
 */
public abstract class MemorySegment
{
	/**
	 * The random access view, used to put elements at arbitrary positions in the memory.
	 */
	public final RandomAccessView randomAccessView;

	/**
	 * The input view, used to read the data sequentially from the memory segment.
	 */
	public final DataInputView inputView;

	/**
	 * The output view, used to write data sequentially to the memory segment.
	 */
	public final DataOutputView outputView;

	/**
	 * The size of the memory segment.
	 */
	protected final int size;

	/**
	 * A flag, indicating whether the segment has been freed.
	 */
	private boolean isFreed;

	
	// -------------------------------------------------------------------------
	//                             Constructors
	// -------------------------------------------------------------------------

	/**
	 * Creates a new memory segment of given size with the provided views.
	 * 
	 * @param size The size of the memory segment.
	 * @param randomAccessView The random access view to use.
	 * @param inputView The input view to use.
	 * @param outputView The output view to use.
	 */
	protected MemorySegment(int size, RandomAccessView randomAccessView, 
			DataInputView inputView, DataOutputView outputView)
	{
		this.randomAccessView = randomAccessView;
		this.inputView = inputView;
		this.outputView = outputView;
		
		this.size = size;
		
		this.isFreed = false;
	}

	// -------------------------------------------------------------------------
	//                               MemorySegment
	// -------------------------------------------------------------------------
	
	/**
	 * Gets the size of the memory segment, in bytes. Because segments
	 * are backed by arrays, they cannot be larger than two GiBytes.
	 * 
	 * @return The size in bytes.
	 */
	public final int size() {
		return size;
	}

	/**
	 * Frees the memory segment. A freed memory segment is invalidated and produces
	 * undefined results, when accessed through any of its views.
	 */
	public final void free() {
		isFreed = true;
	}

	/**
	 * Checks, whether the segment has been freed.
	 * 
	 * @return True, if the segment has been freed, false, if it is still valid.
	 */
	public final boolean isFree() {
		return isFreed;
	}
	
	// -------------------------------------------------------------------------
	//                       Helper methods
	// -------------------------------------------------------------------------
	

	/**
	 * Wraps the chunk of the underlying memory located between <tt>offset<tt> and 
	 * <tt>length</tt> in a NIO ByteBuffer.
	 * 
	 * @param offset The offset in the memory segment.
	 * @param length The number of bytes to be wrapped as a buffer.
	 * @return A <tt>ByteBuffer</tt> backed by the specified portion of the memory segment.
	 * @throws IndexOutOfBoundsException Thrown, if offset is negative or larger than the memory segment size,
	 *                                   or if the offset plus the length is larger than the segment size.
	 */
	public abstract ByteBuffer wrap(int offset, int length);
}
