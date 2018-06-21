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
 * Created: Mauro Botelho <mabotelh@email.com>, 20 Mar 1999.
 */

package grendel.storage.mdb;

/*| nsIMdbCell: the text in a single column of a row.  The base nsIMdbBlob
**| class provides all the interface related to accessing cell text.
**|
**|| column: each cell in a row appears in a specific column, where this
**| column is identified by the an integer mdb_scope value (generated by
**| the StringToScopeToken() method in the containing nsIMdbPort instance).
**| Because a row cannot have more than one cell with the same column,
**| something must give if one calls SetColumn() with an existing column
**| in the same row. When this happens, the other cell is replaced with
**| this cell (and the old cell is closed if it has outstanding refs).
**|
**|| row: every cell instance is a part of some row, and every cell knows
**| which row is the parent row.  (Note this should be represented by a
**| weak backpointer, so that outstanding cell references cannot keep a
**| row open that should be closed. Otherwise we'd have ref graph cycles.)
**|
**|| text: a cell can either be text, or it can have a child row or table,
**| but not both at once.  If text is read from a cell with a child, the text
**| content should be empty (for AliasYarn()) or a description of the type
**| of child (perhaps "mdb:cell:child:row" or "mdb:cell:child:table").
**|
**|| child: a cell might reference another row or a table, rather than text.
**| The interface for putting and getting children rows and tables was first
**| defined in the nsIMdbTable interface, but then this was moved to this cell
**| interface as more natural. 
|*/
interface nsIMdbCell extends nsIMdbBlob { // text attribute in row with column scope
  
  // { ===== begin nsIMdbCell methods =====
  
  // { ----- begin attribute methods -----
  public void SetColumn(nsIMdbEnv ev, int inColumn); 
  public int GetColumn(nsIMdbEnv ev);
  public int GetBlobFill(nsIMdbEnv ev);
  public mdbOid GetChildOid(nsIMdbEnv ev);
  public boolean IsRowChild(nsIMdbEnv ev);
  
  /*
    public mdb_err GetCellInfo(  // all cell metainfo except actual content
    nsIMdbEnv* ev, 
    mdb_column* outColumn,           // the column in the containing row
    mdb_fill*   outBlobFill,         // the size of text content in bytes
    mdbOid*     outChildOid,         // oid of possible row or table child
    mdb_bool*   outIsRowChild) = 0;  // nonzero if child, and a row child
  */
  
  // Checking all cell metainfo is a good way to avoid forcing a large cell
  // in to memory when you don't actually want to use the content.
  
  public nsIMdbRow GetRow(nsIMdbEnv ev); // parent row for this cell
  public nsIMdbPort GetPort(nsIMdbEnv ev); // port containing cell
  // } ----- end attribute methods -----
  
  // { ----- begin children methods -----
  public mdbOid HasAnyChild( // does cell have a child instead of text?
                            nsIMdbEnv ev);
  // out id of row or table (or unbound if no child)
  // nonzero if child is a row (rather than a table)
  public boolean IsChildRow(nsIMdbEnv ev);
  
  public nsIMdbRow GetChildRow( // access table of specific attribute
                               nsIMdbEnv ev); // context
  
  public nsIMdbTable GetChildTable( // access table of specific attribute
                                   nsIMdbEnv ev); // context
  
  
  public void SetChildRow( // access table of specific attribute
                          nsIMdbEnv ev, // context
                          nsIMdbRow ioRow); // inRow must be bound inside this same db port
  
  
  public void SetChildTable( // access table of specific attribute
                            nsIMdbEnv ev, // context
                            nsIMdbTable inTable); // table must be bound inside this same db port
  
  // acquire child table (or nil if no child)
  // } ----- end children methods -----
  
  // } ===== end nsIMdbCell methods =====
};
