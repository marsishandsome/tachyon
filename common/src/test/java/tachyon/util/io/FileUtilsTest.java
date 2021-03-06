/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class FileUtilsTest {
  @Rule
  public TemporaryFolder mTestFolder = new TemporaryFolder();

  @Rule
  public final ExpectedException mException = ExpectedException.none();

  @Test
  public void changeLocalFilePermissionTest() throws IOException {
    File tempFile = mTestFolder.newFile("perm.txt");
    FileUtils.changeLocalFilePermission(tempFile.getAbsolutePath(), "000");
    Assert.assertFalse(tempFile.canRead() && tempFile.canWrite() && tempFile.canExecute());
    FileUtils.changeLocalFilePermission(tempFile.getAbsolutePath(), "777");
    Assert.assertTrue(tempFile.canRead() && tempFile.canWrite() && tempFile.canExecute());
    // File deletion should fail, because we don't have write permissions
    FileUtils.changeLocalFilePermission(tempFile.getAbsolutePath(), "444");
    Assert.assertTrue(tempFile.canRead());
    Assert.assertFalse(tempFile.canWrite());
    Assert.assertFalse(tempFile.canExecute());
    // expect a file permission error when we open it for writing
    mException.expect(IOException.class);
    FileWriter fw = new FileWriter(tempFile);
    if (fw != null) {
      fw.close();
    }
  }

  @Test
  public void changeNonExistentFileTest() throws IOException {
    // ghostFile is never created, so changing permission should fail
    File ghostFile = new File(mTestFolder.getRoot(), "ghost.txt");
    mException.expect(IOException.class);
    FileUtils.changeLocalFilePermission(ghostFile.getAbsolutePath(), "777");
    Assert.assertTrue(ghostFile.delete());
  }

  @Test
  public void changeLocalDirPermissionTests() throws IOException {
    File tempFile = mTestFolder.newFile("perm.txt");
    // Change permission on directories
    FileUtils.changeLocalFilePermission(mTestFolder.getRoot().getAbsolutePath(), "444");
    Assert.assertFalse(tempFile.delete());
    FileUtils.changeLocalFilePermission(mTestFolder.getRoot().getAbsolutePath(), "744");
    Assert.assertTrue(tempFile.delete());
  }

  @Test
  public void moveFileTest() throws IOException {
    File fromFile = mTestFolder.newFile("from.txt");
    File toFile = mTestFolder.newFile("to.txt");
    // Move a file and verify
    FileUtils.move(fromFile.getAbsolutePath(), toFile.getAbsolutePath());
    Assert.assertFalse(fromFile.exists());
    Assert.assertTrue(toFile.exists());
  }

  @Test
  public void deleteFileTest() throws IOException {
    File tempFile = mTestFolder.newFile("fileToDelete");
    File tempFolder = mTestFolder.newFolder("dirToDelete");
    // Delete a file and a directory
    FileUtils.delete(tempFile.getAbsolutePath());
    FileUtils.delete(tempFolder.getAbsolutePath());
    Assert.assertFalse(tempFile.exists());
    Assert.assertFalse(tempFolder.exists());
  }

  @Test
  public void deleteNonExistentFileTest() throws IOException {
    // ghostFile is never created, so deleting should fail
    File ghostFile = new File(mTestFolder.getRoot(), "ghost.txt");
    mException.expect(IOException.class);
    FileUtils.delete(ghostFile.getAbsolutePath());
  }
}
