Subject - BDS Hub: Security Vulnerability Notification for Component Name in Project Name
Subject - BDS Hub: Policy Violation Notification for Component Name in Project Name
Subject - BDS Hub: Policy Violation Override Notification for Component Name in Project Name
Subject - BDS Hub: Policy Violation Override Cancellation Notification for Component Name in Project Name

Dear ${hubUserName},

<#if policyViolations?? && policyViolations?size gt 0>
  The Black Duck Hub's monitoring system captured the following policy violation -

  <ul style="list-style-type: none;">
    <#list policyViolations as policyViolation>
      <li>
      Project Name/Version/Component Name/Component Version
Policy Name in Violation
Policy Conditions
<Actual Policy Logic> text display
      </li>
    </#list>
  </ul>
</#if>

<#if policyViolationOverrides?? && policyViolationOverrides?size gt 0>
  The Black Duck Hub's monitoring system captured the following override of a policy violation -

  <ul style="list-style-type: none;">
    <#list policyViolationOverrides as policyViolationOverride>
      <li>
      Project Name/Version/Component Name/Component Version was overridden
      Policy Name in Violation
      Policy Conditions
      <Actual Policy Logic> text display
      Override by <User Name>
      </li>
    </#list>
  </ul>
</#if>

<#if policyViolationOverrideCancellations?? && policyViolationOverrideCancellations?size gt 0>
  The Black Duck Hub's monitoring system captured the following cancellation of an override of a policy violation -

  <ul style="list-style-type: none;">
    <#list policyViolationOverrideCancellations as policyViolationOverrideCancellation>
      <li>
      </li>
    </#list>
  </ul>
</#if>

<#if securityVulnerabilities?? && securityVulnerabilities?size gt 0>
  The Black Duck Hub's monitoring system captured the following security vulnerability -

  <ul style="list-style-type: none;">
    <#list securityVulnerabilities as securityVulnerability>
      <li>
      Project Name/Version/Component Name/Version
CVE Identifier
Published Date
Base Score
Exploitability
Impact Score
Logged Date
      </li>
    </#list>
  </ul>
</#if>

To manage these items and/or see more details, please log in to your <a href="${hubServerUrl}">Black Duck Hub</a>
