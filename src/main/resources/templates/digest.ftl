<style>
  table {
      border-collapse: collapse; 
  }
  table, th, td {
      border: 1px solid black;
      padding: 3px
  }
  th {
     background-color:lightgray
  }
  td {
    text-align:center;
  }
  p.bold {
    font-weight: bold
  }
</style>

Dear ${hubUserName},
<br />
<br />
The Black Duck Hub's monitoring system captured the following notification data.
<br/>
<h2>Summary</h2>
<h3>Range: ${startDate} to ${endDate}</h3>
<#if projectsDigest?? && projectsDigest.totalsMap?? && projectsDigest.totalsMap?size gt 0>
    <table>
      <tr>
          <th>Total</th>
          <th>Policy Violations</th>
          <th>Policy Overrides</th>
          <th>Vulnerabilities</th>
      </tr>
      <tr>
          <td>${projectsDigest.totalsMap['totalNotifications']}</td>
          <td>${projectsDigest.totalsMap['totalPolicyViolations']}</td>
          <td>${projectsDigest.totalsMap['totalPolicyOverrides']}</td>
          <td>${projectsDigest.totalsMap['totalVulnerabilities']}</td>
      </tr>  
    </table>
</#if>    
<h2>Project/Version Summary</h2>
<ol type="1">
  <#if projectsDigest?? && projectsDigest.projectList?? && projectsDigest.projectList?size gt 0>
      <#list projectsDigest.projectList as projectData>
          <li><a href="${projectData.projectVersionLink}">${projectData.projectName}/${projectData.projectVersionName}</a>
            <div>
              <table>
                <tr>
                    <th>Total</th>
                    <th>Policy Violations</th>
                    <th>Policy Overrides</th>
                    <th>Vulnerabilities</th>
                </tr>
                <tr>
                    <td>${projectData.totalNotificationCount}</td>
                    <td>${projectData.policyViolationCount}</td>
                    <td>${projectData.policyOverrideCount}</td>
                    <td>${projectData.vulnerabilityCount}</td>
                </tr>  
              </table>
              <p class="bold">Policy Violations:${projectData.policyViolations}</p>
              <p class="bold">Policy Overrides: ${projectData.policyOverrides}</p>
              <p class="bold">Vulnerabilities: New: ${projectData.vulnAddedCount} Updated: ${projectData.vulnUpdatedCount} Deleted: ${projectData.vulnDeletedCount}</p> 
            </div>
          </li>
      </#list>
  </#if>
</ol>
To manage these items and/or see more details, please log in to your <a href="${hub_server_url}">Black Duck Hub</a>
<br />
<br />
<img src="cid:${logo_image}" />