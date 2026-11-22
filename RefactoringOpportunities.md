# Refactoring Opportunities — Battleship2

| Local | Code Smell | Refactoring | Student Number |
|---|---|---|---|
| `Fleet::createRandom()` | Separation of Concerns / Complex Method | Extract Method | 123856 |
| `BoardView::BoardView()` | Duplicated Code / Magic Numbers | Introduce Constant | 123856 |
| `Fleet::colisionRisk()` | Poor Naming | Rename | 123856 |
| `Compass::randomBearing()` | Unnecessary Temporary Variable | Inline Variable | 123856 |
| `Fleet::printShips()` | Poor Naming | Change Signature | 123856 |
| `BoardView::BoardView()` | Long Method | Extract Method | 123856 |
| `Game::printBoard` | Long Method | Extract Method | 122626 |
| `Game::getRemainingShips` | Data Flow & Redundancy | Inline Variable | 122626 |
| `Game::repeatedShot` | Feature Envy | Make Static | 122626 |
| `Game::printBoard` | Naming & Readability | Rename | 122626 |
| `PdfExporter::exportarJogadasParaPdf` | Data Flow & Redundancy | Introduce Constant | 122626 |
| `Ship::category` | Encapsulation & API Design | Encapsulate Fields | 122626 |
| `Move::processEnemyFire()` | Long Method | Extract Method | 113183 |
| `Move::processEnemyFire()` | Unnecessary Temporary Variable | Inline Variable | 113183 |
| `Scoreboard::print()` | Magic Numbers | Introduce Constant | 113183 |
| `Scoreboard::print()` | Poor Naming | Rename | 113183 |
| `GameDatabase::loadShots()` | Message Chains | Introduce Variable | 113183 |
