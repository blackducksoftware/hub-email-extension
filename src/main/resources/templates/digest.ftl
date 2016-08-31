Dear ${hubUserName},
<br />
<br />
<#if notificationCounts?? && notificationCounts?size gt 0>
  The Black Duck Hub's monitoring system captured the following data for ${startDate} through ${endDate}.
  <br/>
  <ol type="1">
  <#list notificationCounts as projectCount>
    <#if projectCount?? && projectCount.categoryData?? && projectCount.categoryData?size gt 0>
      <li>${projectCount.category}
        <ul>
        <#list projectCount.categoryData as versionCount>
            <li>${versionCount.category}
              <ul>
                <li>Total Notifications: ${versionCount.categoryData['totalNotificationCount']}</li>
                <li>Policy Violations:   ${versionCount.categoryData['policyViolationCount']}</li>
                <li>Policy Overrides:    ${versionCount.categoryData['policyOverrideCount']}</li>
                <li>Vulnerabilites:      ${versionCount.categoryData['vulnerabilityCount']}
                  <ul>
                      <li>New Vulnerabilites:      ${versionCount.categoryData['vulnAddedCount']}</li>
                      <li>Updated Vulnerabilities: ${versionCount.categoryData['vulnUpdatedCount']}</li>
                      <li>Deleted Vulnerabilities: ${versionCount.categoryData['vulnDeletedCount']}</li>
                  </ul>
                </li>
              </ul>
            </li>
        </#list>
        </ul>
      </li>
    </#if>
  </#list>
  </ol>
</#if>

To manage these items and/or see more details, please log in to your <a href="${hub_server_url}">Black Duck Hub</a>
<br />
<br />
<img src="cid:${logo_image}" />