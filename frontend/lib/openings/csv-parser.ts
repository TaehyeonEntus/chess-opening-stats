export interface OpeningCsvRow {
  id: number;
  eco: string;
  name: string;
  epd: string;
  pgn: string;
}

export type OpeningDictionary = Record<number, Omit<OpeningCsvRow, 'id'>>;

export function parseOpeningCsv(csvText: string): OpeningDictionary {
  const lines = csvText.split(/\r?\n/);
  const dictionary: OpeningDictionary = {};

  // Skip header: id,eco,name,epd,pgn
  for (let i = 1; i < lines.length; i++) {
    const line = lines[i].trim();
    if (!line) continue;

    const columns = [];
    let current = '';
    let inQuotes = false;
    for (let charIndex = 0; charIndex < line.length; charIndex++) {
      const char = line[charIndex];
      if (char === '"') {
        inQuotes = !inQuotes;
      } else if (char === ',' && !inQuotes) {
        columns.push(current.trim());
        current = '';
      } else {
        current += char;
      }
    }
    columns.push(current.trim());

    if (columns.length >= 4) {
      const id = parseInt(columns[0]);
      if (!isNaN(id)) {
        dictionary[id] = {
          eco: columns[1],
          name: columns[2].replace(/^"|"$/g, ''), // remove surrounding quotes
          epd: columns[3],
          pgn: columns[4] || ''
        };
      }
    }
  }

  return dictionary;
}
