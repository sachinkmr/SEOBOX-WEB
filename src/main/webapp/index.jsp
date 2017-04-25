<%-- 
    Document   : index
    Created on : Jan 9, 2017, 12:42:29 PM
    Author     : Sachin Kumar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String address = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().lastIndexOf('/'));
    session.setAttribute("address", address);
    //response.sendRedirect("HistoryPage");
%>
<!DOCTYPE html>
<html>

    <%@include file="/WEB-INF/jspf/head.jspf"%>
    <title>SEOBOX</title>
    <link href="${pageContext.request.contextPath}/assets/css/main.css" rel="stylesheet"> 

    <body role="document">
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-2 sidebar" id="sidebar">
                    <%@include file="/WEB-INF/jspf/sidebar.jspf"%>
                </div>
                <div class="col-md-10" id="main1">
                    <div class="" id="main2">
                        <div class="row">
                            <div class="" id="sachin">
                                <div class="tool-header">
                                    <h1>Get Started...</h1>
                                    <h4>Validate Site SEO checklist and performance</h4>
                                </div>
                            </div>
                            <div class="hidden" id="mask">
                                <div class="col-md-6 col-md-offset-3 mask-data">
                                    <img src="${pageContext.request.contextPath}/assets/img/spin.gif" class="img-responsive center-block"/>     
                                    <h3>Processing...</h3>                                    
                                    <div id="info">
                                        <div id="status"></div>
                                        <div id="msgs"><p></p></div>
                                        <input type="hidden" id="buildID" name="buildID" value="">
                                        <input type="hidden" id="buildUrl" name="buildUrl" value="">                                        
                                        <input type="hidden" id="queueUrl" name="queueUrl" value="">
                                        <input type="hidden" id="type" name="type" value="">
                                        <input type="hidden" id="start" name="start" value="0">
                                    </div>
                                    <div class="hidden" id='bar-mask'>                                        
                                        <div class="progress" >
                                            <div id="orangeBar-mask" class="progress-bar progress-bar-info progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 100%;"></div>
                                        </div>  
                                    </div>
                                </div>
                                <div class='clearfix'></div>
                            </div>
                            <div id="content">
                                <form id="single-site-form" name="single-site-form" action="Build" method="POST" enctype="multipart/form-data"> 
                                    <div id='page' name='page'>
                                        <div class="section">Site Info: </div>
                                        <div class="section-data"> 
                                            <div class="row form-group" >
                                                <label for="siteURL" class="col-md-2 col-form-label">Site Address: </label>
                                                <div class="col-md-10  form-section row">
                                                    <div class="form-group">   
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <span class="glyphicon glyphicon-globe" aria-hidden="true"></span>
                                                            </span>                                        
                                                            <input type="text" class="form-control" placeholder="http://example.com" id="siteURL" name="siteURL" title ="Enter URL with http:// or https://" data-toggle="tooltip">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="form-group row">
                                                <div class="col-md-2" >
                                                    <input type="checkbox" name="setAuthentication" id="setAuthentication">
                                                    <label for="setAuthentication" class="col-form-label"> Authentication</label>
                                                </div> 
                                                <div class="col-md-10  form-section row">
                                                    <div class="form-group col-md-12">   
                                                        <div class="input-group col-md-6 row auth">
                                                            <span class="input-group-addon">
                                                                <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                                                            </span>                                        
                                                            <input type="text" class="form-control" disabled placeholder="Username" id="username" name="username" title ="Enter LDAP username for site" data-toggle="tooltip">
                                                        </div>  
                                                        <div class="input-group col-md-6 row auth">
                                                            <span class="input-group-addon">
                                                                <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                                                            </span>                                        
                                                            <input type="password" class="form-control" disabled placeholder="Password" id="password" name="password" title ="Enter LDAP password for site" data-toggle="tooltip">
                                                        </div>
                                                    </div> 
                                                </div>
                                            </div>                                            
                                           
                                            
                                            <div class=" form-group crawler row">
                                                <label for="fileURL" class="col-md-2 col-form-label">Crawl Config: </label>
                                                <div class="col-md-10  form-section row">
                                                    <div class="form-group ">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <span class="glyphicon glyphicon-file" aria-hidden="true"></span>
                                                            </span>                                        
                                                            <input type="file" class="form-control " placeholder="Config.properties" id="fileURL" name="fileURL" title ="Browse Configuration file for crawler. Config.properties file" data-toggle="tooltip">
                                                        </div>
                                                        <div class=''>
                                                            <div class="hidden" id='bar'>
                                                                <div class="progress" >
                                                                    <div id="orangeBar" class="progress-bar progress-bar-info progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 100%;"></div>
                                                                </div>  
                                                            </div> 
                                                            <div class='clearfix'></div>
                                                        </div> 
                                                        <div class=''>
                                                            <p>View Sample File: <a target="_blank" href="http://10.207.16.9/SEOBOX/Config.properties"> http://10.207.16.9/SEOBOX/Config.properties</a> </p>
                                                            <div class='clearfix'></div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div> 
                                            <div class="row form-group" >
                                                <label for="emails" class="col-md-2 col-form-label">Email: </label>
                                                <div class="col-md-10  form-section row">
                                                    <div class="form-group ">
                                                        <div class="input-group ">
                                                            <span class="input-group-addon">
                                                                <span class="glyphicon glyphicon-envelope" aria-hidden="true"></span>
                                                            </span>                                        
                                                            <input type="text" class="form-control" placeholder="abc@sapient.com" id="email" name="email" title ="Enter Emails seperated by commas" data-toggle="tooltip">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="form-group" >                                               
                                                <div class="col-md-offset-2 col-md-10  form-section ">
                                                    <div class="form-group ">
                                                        <button class="btn btn-primary pull-right col-md-2 sbmt" type="submit">Validate</button>
                                                    </div>
                                                </div>
                                            </div>

                                        </div>
                                        <div class="hidden">
                                            <input type="hidden" name="CrawlerConfigFile" id="CrawlerConfigFile" value="">
                                        </div>
                                    </div>
                                    <div class='my-row '>

                                        <div class="hidden" id='bar'>
                                            <div class="progress" >
                                                <div id="orangeBar" class="progress-bar progress-bar-info progress-bar-striped" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>
                                            </div>  
                                        </div> 
                                        <div class='clearfix'></div>
                                    </div>                                    
                                    <div id="results" class="">
                                        <div class='section'>Results: </div>
                                        <div class="section-data"> 
                                            <div id='resultsData' class="">

                                            </div>
                                        </div>
                                    </div>

                                    <div class='clearfix'></div>                                    
                                </form>
                            </div>
                            <%@include file="/WEB-INF/jspf/footer.jspf"%>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%@include file="/WEB-INF/jspf/footerScripts.jspf"%>
        <script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.fileupload.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.js"></script>  
    </body>
</html>
