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
 * Created: Terry Weissman <terry@netscape.com>, 24 Nov 1997.
 */

package grendel.storage;

import calypso.util.ByteBuf;
import calypso.util.Assert;

import grendel.storage.addressparser.RFC822Mailbox;
import grendel.storage.addressparser.RFC822MailboxList;
import grendel.util.Constants;

import java.io.InputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;

import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeUtility;

class MessageExtraWrapper implements MessageExtra {

  Message m;
  protected MessageExtraWrapper(Message mess) {
    m = mess;
  }

  protected String getSingleAddressName(String headername) {
    String[] list = null;
    try {
      list = m.getHeader("From");
    } catch (MessagingException e) {
    }
    if (list == null || list.length < 1) return null;
    RFC822Mailbox boxes[] = new RFC822MailboxList(list[0]).getMailboxArray();
    if (boxes == null || boxes.length < 1) return null;
    String result = boxes[0].getName();
    if (result == null || result.length() == 0) result = boxes[0].getAddress();
    return result;
  }



  public String getAuthor() {
    return getSingleAddressName("From");
  }

  public String getRecipient() {
    return getSingleAddressName("To");
  }


  // Removes leading "Re:" or similar from the given StringBuffer.  Returns
  // true if it found such a string to remove; false otherwise.
  protected boolean stripRe(StringBuffer buf) {
    // Much of this code is duplicated in MessageBase.  Sigh. ###
    if (buf == null) return false;
    int numToTrim = 0;
    int length = buf.length();
    if (length > 2 &&
        (buf.charAt(0) == 'r' || buf.charAt(0) == 'R') &&
        (buf.charAt(1) == 'e' || buf.charAt(1) == 'E')) {
      char c = buf.charAt(2);
      if (c == ':') {
        numToTrim = 3; // Skip over "Re:"
      } else if (c == '[' || c == '(') {
        int i = 3;    // skip over "Re[" or "Re("

        // Skip forward over digits after the "[" or "(".
        while (i < length &&
               buf.charAt(i) >= '0' &&
               buf.charAt(i) <= '9') {
          i++;
        }
        // Now ensure that the following thing is "]:" or "):"
        // Only if it is do we treat this all as a "Re"-ish thing.
        if (i < (length-1) &&
            (buf.charAt(i) == ']' ||
             buf.charAt(i) == ')') &&
            buf.charAt(i+1) == ':') {
          numToTrim = i+2; // Skip the whole thing.
        }
      }
    }
    if (numToTrim > 0) {
      int i = numToTrim;
      while (i < length - 1 && Character.isWhitespace(buf.charAt(i))) {
        i++;
      }
      for (int j=i ; j<length ; j++) {
        buf.setCharAt(j - i, buf.charAt(j));
      }
      buf.setLength(length - i);
      return true;
    }
    return false;
  }


  public String simplifiedSubject() {
    try {
      String sub = m.getSubject();
      if (sub != null) {
        StringBuffer buf = new StringBuffer(sub);
        if (stripRe(buf)) return buf.toString();
        return sub;
      }
    } catch (MessagingException e) {
    }
    return "";
  }

  public boolean subjectIsReply() {
    try {
      String sub = m.getSubject();
      if (sub != null) {
        StringBuffer buf = new StringBuffer(sub);
        return stripRe(buf);
      }
    } catch (MessagingException e) {
    }
    return false;
  }

  public String simplifiedDate() {
    try {
      Date d = m.getSentDate();
      if (d != null) return MessageBase.SimplifyADate(d.getTime());
    } catch (MessagingException e) {
    }
    return "";
  }

  public Object getMessageID() {
    String[] list = null;
    try {
      list = m.getHeader("Message-ID");
    } catch (MessagingException e) {
    }
    if (list == null || list.length < 1) return null;
    return list[0];
  }

  public Object[] messageThreadReferences() {
    // ### WRONG WRONG WRONG.  This needs to steal the code from
    // MessageBase.internReferences and do clever things with it.
    return null;
  }

  public boolean isRead() {
    try {
      return m.isSet(Flags.Flag.SEEN);
    } catch (MessagingException e) {
      return false;
    }
  }
  public void setIsRead(boolean value) {
    try {
      m.setFlags(new Flags(Flags.Flag.SEEN), value);
    } catch (MessagingException e) {
    }
  }
  public boolean isReplied() {
    try {
      return m.isSet(Flags.Flag.ANSWERED);
    } catch (MessagingException e) {
      return false;
    }
  }
  public void setReplied(boolean value) {
    try {
      m.setFlags(new Flags(Flags.Flag.ANSWERED), value);
    } catch (MessagingException e) {
    }
  }

  public boolean isForwarded() {
// ####      return m.isSet(new Flags("Forwarded"));
// #### this flags crap is all messed up, since Sun munged the APIs.
    Assert.NotYetImplemented("MessageExtraWrapper.isForwarded");
    return false;
  }
  public void setForwarded(boolean value) {
// ####      m.setFlags(new Flags("Forwarded"), value);
// #### this flags crap is all messed up, since Sun munged the APIs.
    Assert.NotYetImplemented("MessageExtraWrapper.setForwarded");
  }

  public boolean isFlagged() {
    try {
      return m.isSet(Flags.Flag.ANSWERED);
    } catch (MessagingException e) {
      return false;
    }
  }

  public void setFlagged(boolean value) {
    try {
      m.setFlags(new Flags(Flags.Flag.ANSWERED), value);
    } catch (MessagingException e) {
    }
  }

  public boolean isDeleted() {
    try {
      return m.isSet(Flags.Flag.DELETED);
    } catch (MessagingException e) {
      return false;
    }
  }
  public void setDeleted(boolean value) {
    try {
      m.setFlags(new Flags(Flags.Flag.DELETED), value);
    } catch (MessagingException e) {
    }
  }

  public InputStream getInputStreamWithHeaders() throws MessagingException {
    InternetHeaders heads = new InternetHeaders();
    Enumeration e;
    for (e = m.getAllHeaders() ; e.hasMoreElements() ;) {
      Header h = (Header) e.nextElement();
      try {
        heads.addHeader(h.getName(), MimeUtility.encodeText(h.getValue()));
      } catch (UnsupportedEncodingException u) {
        heads.addHeader(h.getName(), h.getValue()); // Anyone got a better
                                                    // idea???  ###
      }
    }
    ByteBuf buf = new ByteBuf();
    for (e = heads.getAllHeaderLines() ; e.hasMoreElements() ;) {
      buf.append(e.nextElement());
      buf.append(Constants.BYTEBUFLINEBREAK);
    }
    buf.append(Constants.BYTEBUFLINEBREAK);

    ByteBuf buf2 = new ByteBuf();
    try {
      buf2.append(m.getContent());
    } catch (IOException ee) {
      throw new MessagingException("I/O error", ee);
    }

    return new SequenceInputStream(buf.makeInputStream(),
                                   buf2.makeInputStream());

  }

}
