Summary

    * Status: redirect-on-error property not taken into account
    * CCP Issue: N/A, Product Jira Issue: WS-255.
    * Complexity: middle

The Proposal
Problem description

What is the problem to fix?

    * The redirect-on-error property of the org.exoplatform.services.security.sso.config.SSOConfigurator component (exo.ws.security.spnego.core) is not taken in account. It is always null.
      When starting eXo, the init() method of the SSOAuthenticationFilter filter is called before SSOConfigurator's constructor. So the redirectOnError attribute is not defined yet when the init() method wants to use it.

Fix description

How is the problem fixed?

    * The redirect-on-error property is not taken any more in init() method of SSOAuthenticationFilter. It will be taken from org.exoplatform.services.security.sso.config.SSOConfigurator only to redirect user to alternative authentication if SPNEGO is failed. It fixes the problem since SSOConfigurator is initialized during eXo Container starts and the initialization will be finished before the first request via HTTP.

Patch information:

    * Final files to use should be attached to this page (Jira is for the discussion)

Patch files:
There are currently no attachments on this page.
Tests to perform

Reproduction test

    * Configure authentication (if not configured yet) as described at http://wiki.exoplatform.com/xwiki/bin/view/WS/Kerberos%20SSO%20on%20Active%20Directory. File web.xml of the application which we want to protect must contain servlets with specified <load-on-startup> element otherwise the problem may not appear.
      Try to login from Windows machine which is not in domain (user does not exist in Active Directory) and get "Forbidden" response but should be redirected for alternative authentication since we provide the parameter "redirect-on-error" in configuration.

Tests performed at DevLevel

    * After fixing, repeat the steps described in Reproduction test section. The problem does not appear any more.

Tests performed at QA/Support Level
*
Documentation changes

Documentation changes:

    * No

Configuration changes

Configuration changes:

    * No

Will previous configuration continue to work?

    * Yes

Risks and impacts

Can this bug fix have any side effects on current client projects?

    * No

Is there a performance risk/cost?

    * No

Validation (PM/Support/QA)

PM Comment

    * Patch approved by PM

Support Comment

    * Support review : Validated

QA Feedbacks
*

