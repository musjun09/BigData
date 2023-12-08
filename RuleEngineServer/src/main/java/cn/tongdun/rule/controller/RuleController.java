package cn.tongdun.rule.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.hugegraph.driver.GremlinManager;
import org.apache.hugegraph.driver.HugeClient;
import org.apache.hugegraph.structure.gremlin.Result;
import org.apache.hugegraph.structure.gremlin.ResultSet;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "v1")
@RequestMapping(value = "v1")
public class RuleController {
    @Autowired
    private SqlSessionTemplate template;

    @Autowired
    private HugeClient hugeClient;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hell(){
        return "hello";
    }

    @ApiOperation(value = "获取规则引擎中规则执行的结果", httpMethod = "POST")
    @RequestMapping(value = "/getRuleResult", method = RequestMethod.POST)
    public int getRuleResult(@RequestParam String ruleId, @RequestParam String personId){
        String gremlinSql = template.selectOne("getRule", ruleId);
        GremlinManager gremlin = hugeClient.gremlin();
        ResultSet resultSet = gremlin.gremlin(gremlinSql.replaceAll("\\$personId", personId)).execute();
        Result result = resultSet.iterator().next();
        if(result != null){
            return result.getInt();
        }else {
            return 0;
        }
    }
}
