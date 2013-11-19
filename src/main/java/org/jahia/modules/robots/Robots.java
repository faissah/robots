/**
 * This file is part of Jahia, next-generation open source CMS:
 * Jahia's next-generation, open source CMS stems from a widely acknowledged vision
 * of enterprise application convergence - web, search, document, social and portal -
 * unified by the simplicity of web content management.
 *
 * For more information, please visit http://www.jahia.com.
 *
 * Copyright (C) 2002-2013 Jahia Solutions Group SA. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL (or any later version), you may redistribute this Program in connection
 * with Free/Libre and Open Source Software ("FLOSS") applications as described
 * in Jahia's FLOSS exception. You should have received a copy of the text
 * describing the FLOSS exception, and it is also available here:
 * http://www.jahia.com/license
 *
 * Commercial and Supported Versions of the program (dual licensing):
 * alternatively, commercial and supported versions of the program may be used
 * in accordance with the terms and conditions contained in a separate
 * written agreement between you and Jahia Solutions Group SA.
 *
 * If you are unsure which license is appropriate for your use,
 * please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.robots;

import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.sites.JahiaSitesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
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
    String getRobots(ServletRequest request) throws Exception {
        final List<JCRSiteNode> sites = siteService.getSitesNodeList();

        // get the server name from the request
        final String serverName = request.getServerName();

        // aggregate all sites robots configurations
        StringBuilder robots = new StringBuilder(255);
        for (JCRSiteNode site : sites) {

            // only sites with the jmix:robots mixin interest us
            if (site.isNodeType("jmix:robots")) {

                // next we only want sites that have the same server name property than the one that issued the request
                final String siteServerName = site.getPropertyAsString("j:serverName");
                if (serverName.equals(siteServerName)) {
                    String siteRobots = site.getPropertyAsString("j:robots");

                    // process Disallow clauses
                    siteRobots = processDisallowClauses(siteRobots, site.getPath());

                    robots.append(siteRobots).append('\n');
                }
            }
        }

        return robots.toString();
    }

    /**
     * Process the Disallow clauses to add site path since we consider that users will configure their robots.txt
     * relative to the site's root
     *
     * @param siteRobots the original robots configuration String
     * @param sitePath
     * @return the processed robots configuration where Disallow clauses have been changed to be prefixed by the site
     * path
     */
    static String processDisallowClauses(String siteRobots, String sitePath) {
        if (siteRobots != null) {
            if (sitePath != null) {

                sitePath = sitePath.trim();
                if (!sitePath.isEmpty()) {

                    // remove ending slash if needed
                    final int slashIndex = sitePath.lastIndexOf('/');
                    final int lastChar = sitePath.length() - 1;
                    if (slashIndex == lastChar) {
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
                                if (!clause.startsWith("/")) {
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
