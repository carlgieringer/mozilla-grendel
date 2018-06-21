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
 * Created: Eric Bina <ebina@netscape.com>, 30 Oct 1997.
 */

package grendel.storage.addressparser;

import java.io.*;
import java.util.*;


class RFC822Token
{
  static final int UNKNOWN = 0;
  static final int SPECIAL_CHAR = 1;
  static final int QUOTED_STRING = 2;
  static final int DOMAIN_LITERAL = 3;
  static final int COMMENT = 4;
  static final int ATOM = 5;

  private Object obj;
  private int token_type;

  public RFC822Token(Object obj, int type)
  {
    this.obj = obj;
    if ((type < SPECIAL_CHAR)||(type > ATOM))
    {
      this.token_type = UNKNOWN;
    }
    else
    {
      this.token_type = type;
    }
  }

  public Object getObject()
  {
    return(this.obj);
  }

  public int getType()
  {
    return(this.token_type);
  }

  public boolean isSpecialChar(char sp_char)
  {
    if (this.token_type == SPECIAL_CHAR)
    {
      String str = (String)this.obj;
      char t_char = str.charAt(str.length() - 1);

      if (t_char == sp_char)
      {
        return(true);
      }
    }

    return(false);
  }

  public void printTokenType()
  {
    if (this.token_type == SPECIAL_CHAR)
    {
      System.out.print("Special");
    }
    else if (this.token_type == QUOTED_STRING)
    {
      System.out.print("Quoted String");
    }
    else if (this.token_type == DOMAIN_LITERAL)
    {
      System.out.print("Domain Literal");
    }
    else if (this.token_type == COMMENT)
    {
      System.out.print("Comment");
    }
    else if (this.token_type == ATOM)
    {
      System.out.print("Atom");
    }
    else
    {
      System.out.print("Unknown");
    }
  }


  public void printToken()
  {
    String str = (String)this.getObject();

    System.out.print(str);
    System.out.flush();
  }
}

