import { Pipe, PipeTransform } from '@angular/core';
import { CommonModule } from '@angular/common'; // Import CommonModule

@Pipe({
  name: 'replaceUnderscores',
  standalone: true // Rendre le pipe standalone
})
export class ReplaceUnderscoresPipe implements PipeTransform {
  transform(value: string): string {
    if (value) {
      return value.replace(/_/g, ' ');
    }
    return value;
  }
}
