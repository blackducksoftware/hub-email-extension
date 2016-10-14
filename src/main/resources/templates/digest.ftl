<html>
    <head>
       <style>
        .textlogoblack {
            font-family: OpenSans-Bold;
            font-size: 24px;
            color: #4A4A4A;
        }
        .textlogoblue {
            font-family: OpenSans;
            font-size: 24px;
            color: #73B1F0;
        }
        .emailcategory {
            font-family: OpenSans-Light;
            font-size: 14px;
            color: #445B68;
            float: right;
        }
        .line {
            border: 1px solid #979797;
        }
        .description {
            font-family: OpenSans-Light;
            font-size: 14px;
            color: #445B68;
        }
        a {
            font-family: OpenSans-Light;
            font-size: 14px;
            color: #225786;
        }
        .topic_spacer {
          height: 20px;
        }
        .topic_block {
            background: #DDDDDD;
            margin:0px;
            padding-left: 15px;
            padding-top: 20px;
            padding-bottom: 20px;
        }
        .topic {
            font-family: OpenSans-Semibold;
            font-size: 18px;
            color: #445B68;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .category {
            font-family: OpenSans-Semibold;
            font-size: 14px;
            color: #445B68;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .item {
            font-family: Menlo-Regular;
            font-size: 14px;
            color: #445B68;
            padding-right: 15px;
            display: inline-block;
        }
        .poweredBy {
          font-family: OpenSans;
          font-size: 12px;
          color: #4A4A4A;
        }
        .footerBlack {
            font-family: OpenSans-Light;
            font-size: 14px;
            color: #445B68;
        }
        .footerDuck {
            font-family: OpenSans;
            font-size: 12px;
            color: #4A4A4A;
        }
       </style>
    </head>
    <body style="margin:1cm;width:620px;">
        <#macro displayCount type size> 
          <p class="bold indented">${size} ${type}</p>
        </#macro>
        <#macro moreItems size>
          <#if size gt 10>
                <p>${size - 10} more</p>
          </#if>
        </#macro>
        <div class="header">
            <div class="logo">
              <span class="textlogoblack">Black</span><span class="textlogoblue">Duck</span>
            </div>
            <div class="emailcategory">${emailCategory} DIGEST</div>
        </div> 
        <div class="line"></div>
        <br/>
        <div class="description">Black Duck captured the following new policy violations and vulnerabilities.</div>
        <a href="${hub_server_url}">See more details in the Hub</a>
        <br/>
        <br/>
          <#if topicsList?? && topicsList?size gt 0> 
              <#list topicsList as topic>
                  <div class="topic_block">
                    <div class="topic">${topic.projectName} > ${topic.projectVersion}</div>
                    <#if topic.categoryMap?? && topic.categoryMap?size gt 0> 
                      <#list topic.categoryMap?values as categoryItem>
                        <#if categoryItem.itemList?? && categoryItem.itemList?size gt 0>
                            <#assign categoryType="${categoryItem.categoryKey}">
                            <#if categoryType == "POLICY_VIOLATION">
                              <#assign categoryName="Policy Violations">
                            <#elseif categoryType == "HIGH_VULNERABILITY">
                              <#assign categoryName="High Vulnerabilities">
                            <#elseif categoryType == "MEDIUM_VULNERABILITY">
                              <#assign categoryName="Medium Vulnerabilities">
                            <#elseif categoryType == "LOW_VULNERABILITY">
                              <#assign categoryName="Low Vulnerabilities">
                            <#else>
                              <#assign categoryName="${categoryItem.categoryKey}">
                            </#if>
                            <div class="category">${categoryItem.itemCount} ${categoryName}</div>
                            <#list categoryItem.itemList as item>
                                <#if item.dataSet?? && item.dataSet?size gt 0>
                                   <div>
                                   <#list item.dataSet as itemEntry>
                                       <#assign itemType="${itemEntry.key}">
                                       <#if itemType == "RULE">
                                         <div class="item">Rule: ${itemEntry.value}</div>
                                       <#elseif itemType == "COMPONENT">
                                         <div class="item">Component: ${itemEntry.value}</div>  
                                       <#elseif itemType == "COUNT">
                                         <div class="item">(${itemEntry.value})</div>
                                       <#else>
                                         <div class="item">${itemEntry.key}${itemEntry.value}</div>
                                       </#if>
                                   </#list>
                                   </div>
                                </#if>
                                <@moreItems item.dataSet?size/>
                            </#list>
                        </#if>
                      </#list>
                    </#if>
                  </div>
                  <div class="topic_spacer"></div>
              </#list>
          </#if>
        <div class="footer">
            <img src="cid:${logo_image}" />
            <div style="float:right;">
              <span class="poweredBy">Powered by </span><span class="footerBlack">Black</span><span class="footerDuck">Duck</span>
            </div>
        </div>
    </body>
<html>