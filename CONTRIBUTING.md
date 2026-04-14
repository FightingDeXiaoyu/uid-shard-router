# Contributing

Thanks for considering a contribution.

## Development

1. Use JDK 17 or newer.
2. Compile sources:

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object { $_.FullName })
```

3. Compile and run the self-test suite:

```powershell
$main = Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object { $_.FullName }
$test = Get-ChildItem -Recurse -Filter *.java src\test\java | ForEach-Object { $_.FullName }
javac -d out $main $test
java -cp out io.github.fightingdexiaoyu.uidshardrouter.UidShardRouterSelfTest
```

## Pull requests

- Keep APIs small and explicit.
- Add tests for routing or naming behavior changes.
- Update the README for user-facing changes.
