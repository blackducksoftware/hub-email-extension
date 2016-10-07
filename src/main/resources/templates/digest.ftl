<html>
    <head>
        <style>
          .header .footer {
            display:inline-block;
            width:100%;
          }
          .textlogoblack {
            font-family: OpenSans-Bold;
            font-size: 24px;
            color: #4A4A4A;
            letter-spacing: 0px;
            text-align: left;
            display:inline-block;
            width:100%;
          }
          .textlogoblue {
            font-family: OpenSans;
            font-size: 24px;
            color: #73B1F0;
            letter-spacing: 0px;
            text-align:left;
            display:inline-block
          }
          .emailcategory {
            font-family: OpenSans-Light;
            font-size: 14px;
            color: #445B68;
            letter-spacing: 0px;
            text-align:right;
            float:right;
            display:inline-block
          }
          .line {
            border: 1px solid #979797;
          }
          .description {
            font-family: OpenSans-Light;
            font-size: 14px;
            color: #445B68;
            letter-spacing: 0px;
          }
          a {
            font-family: OpenSans-Light;
            font-size: 14px;
            color: #225786;
            letter-spacing: 0px;
          }
          .topic_block {
            background: #DDDDDD;
          }
          .topic {
            font-family: OpenSans-Semibold;
            font-size: 18px;
            color: #445B68;
            letter-spacing: 0px;
            padding-left:15px;
          }
          .category {
            font-family: OpenSans-Semibold;
            font-size: 14px;
            color: #445B68;
            letter-spacing: 0px;
            padding-left:15px;
          }
          .item {
            font-family: Menlo-Regular;
            font-size: 14px;
            color: #445B68;
            letter-spacing: 0.05px;
            padding-left:15px;
            display:inline-block;
          }
          .poweredby {
            font-family: OpenSans;
            font-size: 12px;
            color: #4A4A4A;
            letter-spacing: 0px;
            float:right;
            display:inline-block;
          }                   
          .captured {
            font-family: OpenSans-Light;
            font-size: 14px;
            color: #445B68;
            letter-spacing: 0px;
            display:inline-block;
          }
        </style>
    </head>
    <body style="margin:1cm">
        <#macro displayCount type size> 
          <p class="bold indented">${size} ${type}</p>
        </#macro>
        <#macro moreItems size>
          <#if size gt 10>
                <p>${size - 10} more</p>
          </#if>
        </#macro>
        <div class="header">
            <div class="textlogoblack">Black<div class="textlogoblue">Duck</div><div class="emailcategory">${emailCategory} DIGEST</div></div> 
        </div>
        <div class="line"></div>
        <br/>
        <div class="description">Black Duck captured the following new policy violations and vulnerabilities.</div>
        <a href="${hub_server_url}">See more details...</a>
        <br/>
        <br/>
          <#if topicsList?? && topicsList?size gt 0> 
              <#list topicsList as topic>
                  <div class="topic_block">
                  <h2 class="topic">${topic.projectKey}</h2>
                  <#if topic.categoryList?? && topic.categoryList?size gt 0> 
                      <#list topic.categoryList as categoryItem>
                        <#if categoryItem.itemList?? && categoryItem.itemList?size gt 0>
                            <h3 class="category">${categoryItem.itemList?size} ${categoryItem.categoryKey}</h3>
                            <#list categoryItem.itemList as item>
                                <#if item.dataMap?? && item.dataMap?size gt 0>
                                   <#list item.dataMap?keys as itemKey>
                                       <div class="item">${item.dataMap[itemKey]} ${itemKey} </div>
                                   </#list>
                                </#if>
                                <@moreItems item.dataMap?size/>
                            </#list>
                        </#if>
                      </#list>
                  </#if>
                  </div>
              </#list>
          </#if>
        <br />
        <div class="footer">
            <img src="cid:${logo_image}" /> <div class="inline poweredby">Powered by <div class="captured">Black</div>Duck</div>
        </div>
    </body>
<html>