/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sparql.cg.demo;

import java.util.List;
import java.util.regex.Pattern;

import sparql.cg.demo.CreateTrieDemo.Replacer;

/**
 * CreateBPMultiTree.
 *
 */
public class CreateBPMultiTree {
  private final List<Configuration> confs;
  private final String file;
  public static int thread_num = 1;
  public CreateBPMultiTree(String file, List<Configuration> confs) {
    this.file = file;
    this.confs = confs;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

  public static class Configuration {
    private Replacer replacer;
    private Pattern pattern;
    private String thread;
    private String file;
    public Replacer getReplacer() {
      return replacer;
    }
    public void setReplacer(Replacer replacer) {
      this.replacer = replacer;
    }
    public Pattern getPattern() {
      return pattern;
    }
    public void setPattern(Pattern pattern) {
      this.pattern = pattern;
    }
    public String getThread() {
      return thread;
    }
    public void setThread(String thread) {
      this.thread = thread;
    }
    public String getFile() {
      return file;
    }
    public void setFile(String file) {
      this.file = file;
    }
  }
}
