Dear ${hubUserName},
<br />
<br />

<#if policyViolationOverrides?? && policyViolationOverrides?size gt 0>
  The Black Duck Hub's monitoring system captured the one or more policy violation overrides -
  <br />

  <ul style="list-style-type: none;">
    <#list policyViolationOverrides as policyViolationOverride>
      <li>
        ${policyViolationOverride.projectName} / ${policyViolationOverride.projectVersionName} / ${policyViolationOverride.componentName} / ${policyViolationOverride.componentVersionName} - ${policyViolationOverride.policyName} was overridden
        <br />
        Overridden by ${policyViolationOverride.firstName} ${policyViolationOverride.lastName}
      </li>
    </#list>
  </ul>
</#if>

To manage these items and/or see more details, please log in to your <a href="${hub_server_url}">Black Duck Hub</a>
<br />
<br />
<img src="cid:${logo_image}" />
