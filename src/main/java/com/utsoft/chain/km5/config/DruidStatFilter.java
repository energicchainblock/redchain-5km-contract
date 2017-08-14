package com.utsoft.chain.km5.config;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
/**
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
@WebFilter(filterName = "druidWebStatFilter", urlPatterns = "/*",
initParams = {@WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")}
)
public class DruidStatFilter {

}
