/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.springframework.lang.Nullable;

/**
 * 为什么存在该类？
 * JDK所提供的访问资源的类（如java.net.URL、File等），并不能很好的满足各种底层资源的访问需求，
 * 比如缺少从类路径或者Web容器的上下文获取资源的操作类。
 *
 * 作用是什么？
 * Spring 设计了一个Resource接口，Resource接口抽象了所有Spring内部使用到的底层资源：File、URL、Classpath等。它为应用提供了更强的底层资源访问能力。
 * 该接口拥有对应不同资源类型的实现类，而且Spring的Resource接口及其实现类可以在脱离Spring框架的情况下使用。
 *
 * Resource接口还提供了不同资源到URL、URI、File类型的转换，以及获取lastModified属性、文件名(不带路径信息的文件名，getFilename())的方法，
 * 为了便于操作，Resource还提供了基于当前资源创建一个相对资源的方法：createRelative()，还提供了getDescription()方法用于在错误处理中的打印信息
 *
 * Spring的资源接口用于抽象对底层资源的访问

 * Interface for a resource descriptor that abstracts from the actual
 * type of underlying resource, such as a file or class path resource.
 *
 * <p>An InputStream can be opened for every resource if it exists in
 * physical form, but a URL or File handle can just be returned for
 * certain resources. The actual behavior is implementation-specific.
 *
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see #getInputStream()
 * @see #getURL()
 * @see #getURI()
 * @see #getFile()
 * @see WritableResource
 * @see ContextResource
 * @see UrlResource  URL资源
 * @see FileUrlResource  file
 * @see FileSystemResource  文件系统
 * @see ClassPathResource 类路径
 * @see ByteArrayResource 数组
 * @see InputStreamResource  流
 */
public interface Resource extends InputStreamSource {

	/**
	 * Determine whether this resource actually exists in physical form.
	 * <p>This method performs a definitive existence check, whereas the
	 * existence of a {@code Resource} handle only guarantees a valid
	 * descriptor handle.
	 *
	 * 判断资源实际上是否物理存在
	 */
	boolean exists();

	/**
	 * Indicate whether non-empty contents of this resource can be read via
	 * {@link #getInputStream()}.
	 * <p>Will be {@code true} for typical resource descriptors that exist
	 * since it strictly implies {@link #exists()} semantics as of 5.1.
	 * Note that actual content reading may still fail when attempted.
	 * However, a value of {@code false} is a definitive indication
	 * that the resource content cannot be read.
	 *
	 * 判断资源是否可读
	 *
	 * @see #getInputStream()
	 * @see #exists()
	 */
	default boolean isReadable() {
		return exists();
	}

	/**
	 * Indicate whether this resource represents a handle with an open stream.
	 * If {@code true}, the InputStream cannot be read multiple times,
	 * and must be read and closed to avoid resource leaks.
	 * <p>Will be {@code false} for typical resource descriptors.
	 *
	 * 返回一个布尔值，指示此资源是否表示具有打开流的句柄。如果为真，则不能多次读取InputStream，必须只读取一次，然后关闭，以避免资源泄漏。
	 * 对于所有常见的资源实现，返回false, InputStreamResource除外。
	 *
	 *  判断资源是否打开
	 *
	 */
	default boolean isOpen() {
		return false;
	}

	/**
	 * Determine whether this resource represents a file in a file system.
	 * A value of {@code true} strongly suggests (but does not guarantee)
	 * that a {@link #getFile()} call will succeed.
	 * <p>This is conservatively {@code false} by default.
	 *
	 * 判断资源是否是文件
	 *
	 * @since 5.0
	 * @see #getFile()
	 */
	default boolean isFile() {
		return false;
	}

	/**
	 * Return a URL handle for this resource.
	 * @throws IOException if the resource cannot be resolved as URL,
	 * i.e. if the resource is not available as descriptor
	 *
	 * 获取资源的URL的句柄
	 *
	 */
	URL getURL() throws IOException;

	/**
	 * Return a URI handle for this resource.
	 * @throws IOException if the resource cannot be resolved as URI,
	 * i.e. if the resource is not available as descriptor
	 *
	 *
	 * 获取资源的URI的句柄
	 *
	 * @since 2.5
	 */
	URI getURI() throws IOException;

	/**
	 * Return a File handle for this resource.
	 * @throws java.io.FileNotFoundException if the resource cannot be resolved as
	 * absolute file path, i.e. if the resource is not available in a file system
	 * @throws IOException in case of general resolution/reading failures
	 * @see #getInputStream()
	 *
	 *  获取资源的文件句柄File
	 *
	 */
	File getFile() throws IOException;

	/**
	 * Return a {@link ReadableByteChannel}.
	 * <p>It is expected that each call creates a <i>fresh</i> channel.
	 * <p>The default implementation returns {@link Channels#newChannel(InputStream)}
	 * with the result of {@link #getInputStream()}.
	 * @return the byte channel for the underlying resource (must not be {@code null})
	 * @throws java.io.FileNotFoundException if the underlying resource doesn't exist
	 * @throws IOException if the content channel could not be opened
	 * @since 5.0
	 * @see #getInputStream()
	 *
	 *
	 * 获取资源的可读字节管道
	 *
	 */
	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}

	/**
	 * Determine the content length for this resource.
	 * @throws IOException if the resource cannot be resolved
	 * (in the file system or as some other known physical resource type)
	 *
	 * 获取资源的长度
	 *
	 */
	long contentLength() throws IOException;

	/**
	 * Determine the last-modified timestamp for this resource.
	 * @throws IOException if the resource cannot be resolved
	 * (in the file system or as some other known physical resource type)
	 *
	 * 获取资源的最后一次修改时间
	 *
	 */
	long lastModified() throws IOException;

	/**
	 * Create a resource relative to this resource.
	 * @param relativePath the relative path (relative to this resource)
	 * @return the resource handle for the relative resource
	 * @throws IOException if the relative resource cannot be determined
	 *
	 * 用于创建相对于当前Resource代表的底层资源的资源，
	 * 比如当前Resource代表文件资源“d:/test/”则createRelative（“test.txt”）
	 * 将返回表文件资源“d:/test/test.txt”Resource资源。
	 *
	 */
	Resource createRelative(String relativePath) throws IOException;

	/**
	 * Determine a filename for this resource, i.e. typically the last
	 * part of the path: for example, "myfile.txt".
	 * <p>Returns {@code null} if this type of resource does not
	 * have a filename.
	 *
	 * 返回当前Resource代表的底层文件资源的文件路径，
	 * 比如File资源“file://d:/test.txt”将返回“d:/test.txt”，
	 * 而URL资源http://www.javass.cn将返回“”，因为只返回文件路径。
	 */
	@Nullable
	String getFilename();

	/**
	 *
	 * 返回此资源的描述，用于处理该资源时的错误输出。这通常是完全限定的文件名或资源的实际URL。
	 *
	 * 返回当前Resource代表的底层资源的描述符，
	 * 通常就是资源的全路径（实际文件名或实际URL地址）。
	 *
	 * Return a description for this resource,
	 * to be used for error output when working with the resource.
	 * <p>Implementations are also encouraged to return this value
	 * from their {@code toString} method.
	 * @see Object#toString()
	 */
	String getDescription();

}
