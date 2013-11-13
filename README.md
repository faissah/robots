# A simple module to manage web robots exclusion configuration files

An overview of what robots exclusion configuration files are and how they work can be found at http://www.robotstxt.org/.

This module adds a panel to the site settings, allowing users to configure the robots exclusion configuration for each site. Each `Disallow` clause is expressed from the site's root URL for greater use.
Once each site's configuration is defined, assuming the server's root domain is `example.com`, accessing `http://example.com/robots.txt` will generate an aggregated version of all configured sites' robots exclusion configuration,
preprending each site's path from the server's root so that robots can properly resolve directories that need to be excluded from crawling.

## Example
Let's assume that we have 2 sites, `mySite` and `mySite2`.

If `mySite`'s robots configuration is:
    
    User-agent: *
    Disallow: /foo
    Disallow: /bar

and `mySite2`'s robots configuration is:
    
    User-agent: foo
    Disallow: /baz

then the resulting `robots.txt` would be:
    
    User-agent: *
    Disallow: /sites/mySite/foo
    Disallow: /sites/mySite/bar
    
    User-agent: foo
    Disallow: /sites/mySite2/baz
