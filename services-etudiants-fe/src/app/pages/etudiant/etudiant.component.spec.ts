import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Etudiant } from './etudiant.component';

describe('Etudiant', () => {
  let component: Etudiant;
  let fixture: ComponentFixture<Etudiant>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Etudiant]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Etudiant);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
