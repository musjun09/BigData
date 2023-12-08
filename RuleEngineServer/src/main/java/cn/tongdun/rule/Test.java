package cn.tongdun.rule;

public class Test {
    public static void main(String[] args) {
        System.out.println("g.V('$personId').out('has_application').has('status', 'OVERDUE').count()".replaceAll("\\$personId", "20001"));
    }
}
