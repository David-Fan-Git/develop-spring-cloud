# Markdown 上下文瘦身设计

## 目标

减少 Claude 初始上下文与常用上下文入口的 token 开支，同时保留 DDD 迁移、构建验证、模块边界和 Git 流程等硬约束。

## 范围

优先处理：
- `CLAUDE.md`
- `.claude/ddd-skills/*.md`
- `docs/superpowers/{plans,reports,specs}/*.md`

不处理应用代码、不改 Maven/Java/SQL 行为。

## 分层策略

1. `CLAUDE.md` 激进压缩：保留项目定位、常用命令、DDD 硬规则、模块结构、Git 流程；删除可从代码推导的长说明和重复背景。
2. DDD skill 保守压缩：保留责任边界、依赖、不变量、验收标准；合并重复模板、长表格和历史叙述。
3. `docs/superpowers` 历史文档索引化：保留文件名和结论入口，减少已完成计划/报告的正文 token。

## 保留规则

- 不删除 DDD 生产就绪标准、模块结构标准、聚合根验收标准中的硬约束。
- 不删除构建/测试命令。
- 不改变业务逻辑说明的含义。
- 对历史计划和报告，只压缩为可追溯摘要，不伪造新事实。

## 验证

- 统计目标 Markdown 修改前后行数。
- 检查关键短语仍存在：`domain`、`application`、`infrastructure`、`service/dal`、`local/remote`、`mvn`、`PR`。
- 使用 `git diff --check` 验证 Markdown 格式无明显空白错误。
