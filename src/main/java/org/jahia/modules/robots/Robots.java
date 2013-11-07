/*
* JBoss, a division of Red Hat
* Copyright 2012, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.jahia.modules.robots;

import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.sites.JahiaSitesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Christophe Laprun
 */
@Controller
public class Robots {
    private static final JahiaSitesService siteService = JahiaSitesService.getInstance();
    public static final String DISALLOW = "Disallow";

    @RequestMapping(method = RequestMethod.GET, value = "/robots", produces = "text/plain")
    public
    @ResponseBody
    String getRobots() throws Exception {
        final List<JCRSiteNode> sites = siteService.getSitesNodeList();

        // aggregate all sites robots configurations
        StringBuilder robots = new StringBuilder(255);
        for (JCRSiteNode site : sites) {

            // only sites with the jmix:robots mixin interest us
            if (site.isNodeType("jmix:robots")) {
                String siteRobots = site.getPropertyAsString("robots");

                // process Disallow clauses
                siteRobots = processDisallowClauses(siteRobots, site.getPath());

                robots.append(siteRobots).append('\n');
            }
        }

        return robots.toString();
    }

    /**
     * Process the Disallow clauses to add site path since we consider that users will configure their robots.txt relative to the site's root
     *
     * @param siteRobots the original robots configuration String
     * @param sitePath
     * @return the processed robots configuration where Disallow clauses have been changed to be prefixed by the site path
     */
    static String processDisallowClauses(String siteRobots, String sitePath) {
        if (siteRobots != null) {
            if (sitePath != null) {

                sitePath = sitePath.trim();
                if (!sitePath.isEmpty()) {

                    // remove ending slash if needed
                    final int slashIndex = sitePath.lastIndexOf('/');
                    final int lastChar = sitePath.length() - 1;
                    if(slashIndex == lastChar) {
                        sitePath = sitePath.substring(0, lastChar);
                    }

                    // per http://www.w3.org/TR/html4/appendix/notes.html#h-B.4.1.1, records are separated by blank lines
                    final String[] split = siteRobots.split("\\r?\\n|\\r");

                    StringBuilder result = new StringBuilder(100 * split.length);
                    for (String s : split) {
                        // only process Disallow clauses
                        int index = s.indexOf(DISALLOW);
                        if (index >= 0) {
                            // if we don't have a column, ignore the entry
                            final int columnIndex = s.indexOf(':');
                            if (columnIndex > 0) {
                                // we have a column, extract the clause
                                String clause = s.substring(columnIndex + 1).trim();

                                // prepend slash if needed
                                if(!clause.startsWith("/")) {
                                    clause = "/" + clause;
                                }

                                // create the modified output
                                result.append("Disallow: ").append(sitePath).append(clause);
                            }
                        } else {
                            // we're not processing a Disallow clause so just output the entry as-is
                            result.append(s);
                        }
                        result.append('\n');
                    }
                    return result.toString();
                }
            }

            // if we were not given a sitePath, just return the input string as-is
            return siteRobots;
        }

        return null;
    }
}
