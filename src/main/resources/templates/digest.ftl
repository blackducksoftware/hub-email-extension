<style>
  a.indented {
      margin-top: 0px;
      margin-bottom: 0px;
      margin-left: 10px;
      margin-right: 0px;
      padding: 0px;
  }
  p.indented {
      margin-top: 0px;
      margin-bottom: 0px;
      margin-left: 10px;
      margin-right: 0px;
      padding: 0px;
  }
  p.bold {
      padding-top: 5px;
      font-weight: bold;
  }
  p.project {
      font-size: 110%;
      margin-top: 0px;
      margin-bottom: 0px;
      margin-left: 0px;
      margin-right: 0px;
  }
</style>

<#macro displayCount type size> 
  <p class="bold indented">${size} ${type}</p>
</#macro>
<#macro moreItemsLink size projectVersionID>
  <#if size gt 10>
      <#if projectVersionID?? && projectVersionID?trim?length gt 0>
        <a class="indented" href="${hub_server_url}/#versions/id:${projectVersionID}/view:bom">${size - 10} more</a>
      <#else>
        <p class="indented">${size - 10} more</p>
      </#if>
  </#if>
</#macro>

Dear ${hubUserName},
<br />
<br />
The Black Duck Hub's ${emailCategory} Digest.
<br/>
<ul style="list-style-type: none;">
  <#if projectsDigest?? && projectsDigest.projectList?? && projectsDigest.projectList?size gt 0>
      <#list projectsDigest.projectList as projectDigest>
          <li><p class="bold project">${projectDigest.projectData['projectName']} > ${projectDigest.projectData['projectVersionName']}</p>
                <#if projectDigest.policyViolations?? && projectDigest.policyViolations?size gt 0>
                    <@displayCount size=projectDigest.projectData['policyViolationCount'] type="Policy Violations"/>
                    <#list projectDigest.policyViolations as policyViolation>
                        <p class="indented">RULE: ${policyViolation['policyName']} COMPONENT: ${policyViolation['componentName']} ${policyViolation['componentVersion']}</p>
                       <#if policyViolation?counter == 10><#break></#if>
                    </#list>
                    <@moreItemsLink projectDigest.policyViolations?size projectDigest.projectData['projectVersionID']/>
                </#if>
                <#if projectDigest.policyOverrides?? && projectDigest.policyOverrides?size gt 0>
                    <@displayCount size=projectDigest.projectData['policyOverrideCount'] type="Policy Overrides"/>
                    <#list projectDigest.policyOverrides as policyOverride>
                       <p class="indented">RULE: ${policyOverride['policyName']} COMPONENT: ${policyOverride['componentName']} ${policyOverride['componentVersion']} By: ${policyOverride['firstName']} ${policyOverride['lastName']}</p>    
                       <#if policyOverride?counter == 10><#break></#if>
                    </#list>
                    <@moreItemsLink projectDigest.policyOverrides?size projectDigest.projectData['projectVersionID']/>
                </#if>
                <#if projectDigest.vulnerabilities?? && projectDigest.vulnerabilities?size gt 0>
                    <@displayCount size=projectDigest.vulnerabilities?size type="Vulnerabilities"/>
                    <#list projectDigest.vulnerabilities as vulnerability>
                       <p class="indented">COMPONENT: ${vulnerability['componentName']} ${vulnerability['componentVersion']} Total: ${vulnerability['vulnTotalCount']} High: ${vulnerability['vulnHighCount']} Medium: ${vulnerability['vulnMediumCount']} Low: ${vulnerability['vulnLowCount']}</p>
                       <#if vulnerability?counter == 10><#break></#if>
                    </#list>
                    <@moreItemsLink projectDigest.vulnerabilities?size projectDigest.projectData['projectVersionID']/>
                </#if>
          </li>
      </#list>
  </#if>
</ul>
To manage these items and/or see more details, please log in to your <a href="${hub_server_url}">Black Duck Hub</a>
<br />
<br />
<img src="cid:${logo_image}" />