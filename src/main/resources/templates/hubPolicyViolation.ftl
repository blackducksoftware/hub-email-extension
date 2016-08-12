Dear ${hubUserName},
<br />
<br />
<#if policyViolations?? && policyViolations?size gt 0>
  The Black Duck Hub's monitoring system captured one or more policy violations -
  <br />

  <ul style="list-style-type: none;">
    <#list policyViolations as policyViolation>
      <li>
        ${policyViolation.projectName} / ${policyViolation.projectVersionName} / ${policyViolation.componentName} / ${policyViolation.componentVersionName} - ${policyViolation.policyName}
      </li>
    </#list>
  </ul>
</#if>

To manage these items and/or see more details, please log in to your <a href="${hub_server_url}">Black Duck Hub</a>
<br />
<br />
<img src="cid:${logo_image}" />
