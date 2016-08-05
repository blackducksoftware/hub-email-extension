Dear ${hubUserName},
<br />
<br />
<#if policyViolations?? && policyViolations?size gt 0>
  The Black Duck Hub's monitoring system captured one or more policy violations -
  <br />

  <ul style="list-style-type: none;">
    <#list policyViolations as policyViolation>
      <li>
        ${policyViolation.projectName} / ${policyViolation.projectVersionName} / ${policyViolation.componentName} / ${policyViolation.componentVersionName}     
        <br />
        Policy Name in Violation: ${policyViolation.policyName}
        <br />
        <!--
        Policy Conditions
        <br />
        Actual Policy Logic text display
        <br />
        ---end unknown values---
        -->
      </li>
    </#list>
  </ul>
</#if>

<#if policyViolationOverrides?? && policyViolationOverrides?size gt 0>
  The Black Duck Hub's monitoring system captured the one or more policy violation overrides -
  <br />

  <ul style="list-style-type: none;">
    <#list policyViolationOverrides as policyViolationOverride>
      <li>
        ${policyViolationOverride.projectName} / ${policyViolationOverride.projectVersionName} / ${policyViolationOverride.componentName} / ${policyViolationOverride.componentVersionName} was overriden
        <br />
        <br />
        Policy Name in Violation: ${policyViolationOverride.policyName}
        <br />
        <!--
        Policy Conditions
        <br />
        Actual Policy Logic text display
        <br />
        ---end unknown values---
        -->
        <br />
        Override by ${policyViolationOverride.firstName} ${policyViolationOverride.lastName}
      </li>
    </#list>
  </ul>
</#if>

<#if policyViolationOverrideCancellations?? && policyViolationOverrideCancellations?size gt 0>
  The Black Duck Hub's monitoring system captured the following cancellation of an override of a policy violation -
  <br />

  <ul style="list-style-type: none;">
    <#list policyViolationOverrideCancellations as policyViolationOverrideCancellation>
      <li>
        ---don't know how to get ANY values---
      </li>
    </#list>
  </ul>
</#if>

<#if securityVulnerabilities?? && securityVulnerabilities?size gt 0>
  The Black Duck Hub's monitoring system captured the following security vulnerability -
  <br />

  <ul style="list-style-type: none;">
    <#list securityVulnerabilities as securityVulnerability>
      <li>
        ${securityVulnerability.projectName} / ${securityVulnerability.projectVersionName} / ${securityVulnerability.componentName} / ${securityVulnerability.componentVersionName}
        <!--
        <br />
        ---don't know how to get these values---
        <br />
        CVE Identifier
        <br />
        Published Date
        <br />
        Base Score
        <br />
        Exploitability
        <br />
        Impact Score
        <br />
        Logged Date
        <br />
        ---end unknown values---
        -->
      </li>
    </#list>
  </ul>
</#if>

To manage these items and/or see more details, please log in to your <a href="${hub_server_url}">Black Duck Hub</a>
<br />
<br />
<img src="cid:${logo_image}" />
