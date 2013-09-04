package com.tempodb.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;


public class PageLinks {

  private static final String HEADER_LINK = "Link"; //$NON-NLS-1$

  private static final String META_REL = "rel";

  private static final String META_NEXT = "next"; //$NON-NLS-1$

  private static final String DELIM_LINKS = ","; //$NON-NLS-1$

  private static final String DELIM_LINK_PARAM = ";"; //$NON-NLS-1$

  private String next = "";

  public PageLinks(HttpResponse response) {
    Header linkHeader = response.getFirstHeader(HEADER_LINK);
    if(linkHeader != null) {
      String[] links = linkHeader.getValue().split(DELIM_LINKS);
      for(String link : links) {
        String[] segments = link.split(DELIM_LINK_PARAM);
        if(segments.length < 2) {
          continue;
        }

        String linkPart = segments[0].trim();
        if(!linkPart.startsWith("<") || !linkPart.endsWith(">")) {
          continue;
        }
        linkPart = linkPart.substring(1, linkPart.length() - 1);

        for(int i = 1; i < segments.length; i++) {
          String rel[] = segments[i].trim().split("=");
          if(rel.length < 2 || !META_REL.equals(rel[0])) {
            continue;
          }

          String relValue = rel[1];
          if(relValue.startsWith("\"") && relValue.endsWith("\"")) {
            relValue = relValue.substring(1, relValue.length() - 1);
          }

          if(META_NEXT.equals(relValue)) {
            next = linkPart;
          }
        }
      }
    }
  }

  public String getNext() {
    return next;
  }
}
