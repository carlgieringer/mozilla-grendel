/* -*- Mode: java; indent-tabs-mode: nil; c-basic-offset: 2 -*-
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is the Grendel mail/news client.
 *
 * The Initial Developer of the Original Code is Netscape Communications
 * Corporation.  Portions created by Netscape are Copyright (C) 1997
 * Netscape Communications Corporation.  All Rights Reserved.
 *
 * Class SearchResultsFolderFactory.
 *
 * Factory for SearchResultsFolder, which at the moment is just a special
 * instance of FilterFolder.
 *
 * Created: David Williams <djw@netscape.com>,  1 Oct 1997.
 */
package grendel.storage;

import javax.mail.Folder;
import javax.mail.search.SearchTerm;

import grendel.storage.FilterFolder;

public class SearchResultsFolderFactory {
  static private FilterFolder fSearchResults = null;
  static public Folder Get() {
        if (fSearchResults == null)
          fSearchResults = new FilterFolder(null, "Search Results", null);
        return fSearchResults;
  }
  static public void SetTarget(Folder folder, SearchTerm term) {
        FilterFolder ff = (FilterFolder)Get();
        ff.setTarget(folder, term);
  }
}
