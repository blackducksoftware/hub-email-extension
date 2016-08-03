<h1>${title}</h1>
<h1>Testing 1, 2, 3</h1>
<p>${message}</p>
<h3>Listing</h3>
<#list items as item>
    ${item_index + 1}: ${item} <br/>
</#list>
<img src="cid:${logo_image}" />
