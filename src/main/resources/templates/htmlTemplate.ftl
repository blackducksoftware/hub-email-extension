<h1>${title}</h1>
<p>${message}</p>
<h3>Listing</h3>
<#list items as item>
    ${item_index + 1}: ${item} <br/>
</#list>
