Dear ${hubUserName},
<br />
<br />
<#if notificationCounts?? && notificationCounts?size gt 0>
  The Black Duck Hub's monitoring system captured the following data for ${startDate} through ${endDate}.
  <br/>
  <dl>
  <#list notificationCounts as projectCount>
    <#if projectCount?? && projectCount.categoryData?? && projectCount.categoryData?size gt 0>
      <dt>${projectCount.category}</dt>
      <dd>
        <dl>
        <#list projectCount.categoryData as versionCount>
          <dt>${versionCount.category}</dt>
          <dd>
            <ul>
              <li>Total Notifications: ${versionCount.categoryData['totalNotificationCount']}</li>
              <li>Policy Violations:   ${versionCount.categoryData['policyViolationCount']}</li>
              <li>Policy Overrides:    ${versionCount.categoryData['policyOverrideCount']}</li>
              <li>Vulnerabilites:      ${versionCount.categoryData['vulnerabilityCount']}</li>
              <li>
                <ul>
                    <li>New Vulnerabilites:      ${versionCount.categoryData['vulnAddedCount']}</li>
                    <li>Updated Vulnerabilities: ${versionCount.categoryData['vulnUpdatedCount']}</li>
                    <li>Deleted Vulnerabilities: ${versionCount.categoryData['vulnDeletedCount']}</li>
                </ul>
              </li>
            </ul>
          </dd>
        </#list>
        </dl>
      </dd>
    </#if>
  </#list>
  </dl>
</#if>

To manage these items and/or see more details, please log in to your <a href="${hub_server_url}">Black Duck Hub</a>
<br />
<br />
<img src="cid:${logo_image}" />