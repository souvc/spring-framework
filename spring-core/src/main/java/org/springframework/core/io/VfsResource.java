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

import org.springframework.core.NestedIOException;
import org.springframework.util.Assert;

/**
 *
 * 代表JBoss的虚拟文件系统VFS
 *
 * 资源以虚拟文件VFS的形式存在，可以使用该实现类。VFS是一个虚拟文件系统，
 * Linux的系统中所有文件的顶层都设计为虚拟的VFS，它能一致的访问物理文件系统、jar资源、zip资源、war资源等，
 * VFS能把这些资源一致的映射到一个目录上，访问它们就像访问物理文件资源一样，而其实这些资源不存在于物理文件系统。
 *
 * 该实现类，封装了一个Object对象，所有的操作都是通过这个包装的对象的反射来实现的。
 * 当然，内部具体实现细节，可以通过工具类 VfsUtils 调用。
 *
 * JBoss VFS based {@link Resource} implementation.
 *
 * <p>As of Spring 4.0, this class supports VFS 3.x on JBoss AS 6+
 * (package {@code org.jboss.vfs}) and is in particular compatible with
 * JBoss AS 7 and WildFly 8+.
 *
 * @author Ales Justin
 * @author Juergen Hoeller
 * @author Costin Leau
 * @author Sam Brannen
 * @since 3.0
 * @see org.jboss.vfs.VirtualFile
 */
public class VfsResource extends AbstractResource {

	private final Object resource;


	/**
	 * Create a new {@code VfsResource} wrapping the given resource handle.
	 * @param resource a {@code org.jboss.vfs.VirtualFile} instance
	 * (untyped in order to avoid a static dependency on the VFS API)
	 */
	public VfsResource(Object resource) {
		Assert.notNull(resource, "VirtualFile must not be null");
		this.resource = resource;
	}


	@Override
	public InputStream getInputStream() throws IOException {
		return VfsUtils.getInputStream(this.resource);
	}

	@Override
	public boolean exists() {
		return VfsUtils.exists(this.resource);
	}

	@Override
	public boolean isReadable() {
		return VfsUtils.isReadable(this.resource);
	}

	@Override
	public URL getURL() throws IOException {
		try {
			return VfsUtils.getURL(this.resource);
		}
		catch (Exception ex) {
			throw new NestedIOException("Failed to obtain URL for file " + this.resource, ex);
		}
	}

	@Override
	public URI getURI() throws IOException {
		try {
			return VfsUtils.getURI(this.resource);
		}
		catch (Exception ex) {
			throw new NestedIOException("Failed to obtain URI for " + this.resource, ex);
		}
	}

	@Override
	public File getFile() throws IOException {
		return VfsUtils.getFile(this.resource);
	}

	@Override
	public long contentLength() throws IOException {
		return VfsUtils.getSize(this.resource);
	}

	@Override
	public long lastModified() throws IOException {
		return VfsUtils.getLastModified(this.resource);
	}

	@Override
	public Resource createRelative(String relativePath) throws IOException {
		if (!relativePath.startsWith(".") && relativePath.contains("/")) {
			try {
				return new VfsResource(VfsUtils.getChild(this.resource, relativePath));
			}
			catch (IOException ex) {
				// fall back to getRelative
			}
		}

		return new VfsResource(VfsUtils.getRelative(new URL(getURL(), relativePath)));
	}

	@Override
	public String getFilename() {
		return VfsUtils.getName(this.resource);
	}

	@Override
	public String getDescription() {
		return "VFS resource [" + this.resource + "]";
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof VfsResource &&
				this.resource.equals(((VfsResource) other).resource)));
	}

	@Override
	public int hashCode() {
		return this.resource.hashCode();
	}

}
