
import { Vec2D, LineSegment2D } from './simplegeometry.js';
import * as assert from "assert";
describe('TestGeometry', () => {
    function checkAllNotIntersects(a, b) {
        assert.strictEqual(a.intersects(b), null);
        assert.strictEqual(a.swap().intersects(b), null);
        assert.strictEqual(a.intersects(b.swap()), null);
        assert.strictEqual(a.swap().intersects(b.swap()), null);
    }

    function checkAllIntersects(a, b) {
        assert.notStrictEqual(a.intersects(b), null);
        assert.notStrictEqual(a.swap().intersects(b), null);
        assert.notStrictEqual(a.intersects(b.swap()), null);
        assert.notStrictEqual(a.swap().intersects(b.swap()), null);
    }

    it('should test segment intersect', () => {
        const segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
        const segmB = new LineSegment2D(new Vec2D(-1.0, 1.0), new Vec2D(1.0, -1.0));
        const pt = segmA.intersects(segmB);
        checkAllIntersects(segmA, segmB);
        assert.ok(pt.sub(Vec2D.ZERO).length() < 0.01);
    });

    it('should test segment not intersect', () => {
        const segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
        const segmB = new LineSegment2D(new Vec2D(-2.0, -2.0), new Vec2D(-1.5, -1.5));
        checkAllNotIntersects(segmA, segmB);
    });

    it('should test segment intersect if parallel', () => {
        let segmA, segmB;

        segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
        segmB = new LineSegment2D(new Vec2D(-2.0, -2.0), new Vec2D(-1.0, -1.0));
        checkAllIntersects(segmA, segmB);

        segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
        segmB = new LineSegment2D(new Vec2D(-2.0, -2.0), new Vec2D(2.0, 2.0));
        checkAllIntersects(segmA, segmB);

        segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
        segmB = new LineSegment2D(new Vec2D(-0.5, -0.5), new Vec2D(2.0, 2.0));
        checkAllIntersects(segmA, segmB);

        segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
        segmB = new LineSegment2D(new Vec2D(-0.5, -0.5), new Vec2D(-0.5, -0.5));
        checkAllIntersects(segmA, segmB);
    });


    it('should test segment intersect in end point', () => {
        const segmA = new LineSegment2D(new Vec2D(-1.0, 1.0), new Vec2D(1.0, 1.0));
        const segmB = new LineSegment2D(new Vec2D(-2.0, -2.0), new Vec2D(1.0, 1.0));
        const pt = segmA.intersects(segmB);
        checkAllIntersects(segmA, segmB);
        assert.ok(pt.sub(new Vec2D(1.0, 1.0)).length() < 0.01);
    });

    it('should test segment intersect in point', () => {
        const segmA = new LineSegment2D(new Vec2D(-2.0, -2.0), new Vec2D(1.0, 1.0));
        const segmB = new LineSegment2D(new Vec2D(1.0, 1.0), new Vec2D(3.0, -1.0));
        const pt = segmA.intersects(segmB);
        checkAllIntersects(segmA, segmB);
        assert.ok(pt.sub(new Vec2D(1.0, 1.0)).length() < 0.01);
    });

    it('should test segment no intersect case', () => {
        const segmA = new LineSegment2D(new Vec2D(-341.0, -223.0), new Vec2D(-292.0, -194.0));
        const segmB = new LineSegment2D(
            new Vec2D(445.8586189801885, 246.7999999999996),
            new Vec2D(447.3242977416373, 243.5499999999996)
        );
        checkAllNotIntersects(segmA, segmB);
    });

    it('should test segment normal vector intersect all', () => {
        for (let i = 0; i < 100; i++) {
            const nX = (Math.random() + 0.1) * 10.0 * randomSign();
            const nY = (Math.random() + 0.1) * 10.0 * randomSign();

            const eX = (Math.random() + 0.1) * 10.0 * randomSign();
            const eY = (Math.random() + 0.1) * 10.0 * randomSign();

            const segmA = new LineSegment2D(new Vec2D(nX, nY), new Vec2D(nX + eX, nY + eY));
            const vec = segmA.vector();
            const norm = new Vec2D(vec.y, vec.x).unit();
            const segmB = new LineSegment2D(
                (segmA.start.add(segmA.end)).div(2.0).sub(norm.mul(20.0)),
                (segmA.start.add(segmA.end)).div(2.0).add(norm.mul(20.0))
            );
            checkAllIntersects(segmA, segmB);
        }
    });

    it('should test not in segment', () => {
        const segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(2.0, 2.0));

        for (let i = 0; i < 100; i++) {
            const n = Math.random() - 0.5;
            const pt = new Vec2D(n, n + 0.25);
            assert.ok(!segmA.inSegment(pt), `${pt} in segment`);
        }

        for (let i = 0; i < 100; i++) {
            const n = Math.random();
            const pt = new Vec2D(n - 10, n - 10);
            assert.ok(!segmA.inSegment(pt), `${pt} in segment`);
        }

        const segm2 = new LineSegment2D(new Vec2D(-2.0, -1.0), new Vec2D(2.0, 1.0));

        for (let i = 0; i < 100; i++) {
            const n = (Math.random() - 0.5) * 2;
            const pt = new Vec2D(2 * n, n + 0.3);
            assert.ok(!segm2.inSegment(pt), `${pt} in segment`);
        }
    });

    it('should test closest point', () => {
        const segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(2.0, 2.0));

        let closest = segmA.closestPoint(new Vec2D(3.0, 3.0));
        assert.ok(closest.sub(new Vec2D(2.0, 2.0)).length() < 0.01, `Closest point was ${closest}`);

        closest = segmA.closestPoint(new Vec2D(-2.0, -2.0));
        assert.ok(closest.sub(new Vec2D(-1.0, -1.0)).length() < 0.01, `Closest point was ${closest}`);

        closest = segmA.closestPoint(new Vec2D(0.0, 0.0));
        assert.ok(closest.sub(new Vec2D(0.0, 0.0)).length() < 0.01, `Closest point was ${closest}`);

        closest = segmA.closestPoint(new Vec2D(0.0, 0.0));
        assert.ok(closest.sub(new Vec2D(0.0, 0.0)).length() < 0.01, `Closest point was ${closest}`);
    });

    it('should test closest point case', () => {
        const segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(2.0, 2.0));

        for (let i = 0; i < 100; i++) {
            const v = segmA.vector();
            const n = v.normalVector().mul(randomSign());

            const p = segmA.start.add(v.mul(Math.random())); // Pick a point along the line
            const check = p.add(n.mul(100.0)); // Move along the normal from that point

            const closest = segmA.closestPoint(check);
            assert.ok(closest.sub(p).length() < 0.01, `Closest point was ${closest} expected ${p} for ${check}`);
        }

        for (let i = 0; i < 100; i++) {
            const v = segmA.vector();
            const n = v.normalVector().mul(randomSign());

            const p = segmA.start.add(v.mul(-Math.random() * 100)); // Pick a point before start
            const check = p.add(n.mul(100.0)); // Move along the normal from that point

            const closest = segmA.closestPoint(check);
            assert.ok(
                closest.sub(segmA.start).length() < 0.01,
                `Closest point was ${closest} expected ${p} for ${check}`
            );
        }

        for (let i = 0; i < 100; i++) {
            const v = segmA.vector();
            const n = v.normalVector().mul(randomSign());

            const p = segmA.start.add(v.mul(1 + Math.random() * 100)); // Pick a point after end
            const check = p.add(n.mul(100.0)); // Move along the normal from that point

            const closest = segmA.closestPoint(check);
            assert.ok(
                closest.sub(segmA.end).length() < 0.01,
                `Closest point was ${closest} expected ${p} for ${check}`
            );
        }
    });


    it('should test case not in segment', () => {
        const segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(2.0, 2.0));
        const pt = new Vec2D(-9.829257132209216, -9.829257132209216);
        assert.ok(!segmA.inSegment(pt), `${pt} in segment`);

        const pt2 = new Vec2D(9.829257132209216, 9.829257132209216);
        assert.ok(!segmA.inSegment(pt2), `${pt2} in segment`);

        const pt3 = new Vec2D(2.02, 2.02);
        assert.ok(!segmA.inSegment(pt3), `${pt3} in segment`);

        const pt4 = new Vec2D(-1.01, 1.01);
        assert.ok(!segmA.inSegment(pt4), `${pt4} in segment`);
    });

    it('should test case not in segment (alternative)', () => {
        const segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(2.0, 2.0));

        const pt = new Vec2D(-9.829257132209216, -9.829257132209216);
        assert.ok(!segmA.inSegment(pt), `${pt} in segment`);

        const pt2 = new Vec2D(9.829257132209216, 9.829257132209216);
        assert.ok(!segmA.inSegment(pt2), `${pt2} in segment`);

        const pt3 = new Vec2D(2.02, 2.02);
        assert.ok(!segmA.inSegment(pt3), `${pt3} in segment`);

        const pt4 = new Vec2D(-1.01, 1.01);
        assert.ok(!segmA.inSegment(pt4), `${pt4} in segment`);
    });

    it('should test not in segment', () => {
        const segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(2.0, 2.0));

        for (let i = 0; i < 100; i++) {
            const n = Math.random() + 0.1;
            const pt = new Vec2D(n, n + 0.25);
            assert.ok(!segmA.inSegment(pt), `${pt} in segment`);
        }

        for (let i = 0; i < 100; i++) {
            const n = Math.random();
            const pt = new Vec2D(n - 10, n - 10);
            assert.ok(!segmA.inSegment(pt), `${pt} in segment`);
        }

        const segm2 = new LineSegment2D(new Vec2D(-2.0, -1.0), new Vec2D(2.0, 1.0));

        for (let i = 0; i < 100; i++) {
            const n = (Math.random() - 0.5) * 2;
            const pt = new Vec2D(2 * n, n + 0.3);
            assert.ok(!segm2.inSegment(pt), `${pt} in segment`);
        }
    });

    it('should test parallel and no intersect', () => {
        for (let i = 0; i < 100; i++) {
            const n_x = (Math.random() + 0.1) * 10.0 * randomSign();
            const e_x = (Math.random() + 0.1) * 10.0 * randomSign();
            const segmA = new LineSegment2D(new Vec2D(n_x, n_x), new Vec2D(n_x + e_x, e_x));
            const norm = segmA.vector().normalVector() * randomSign() * (Math.random() + 0.1) * 1000.0;

            const vecDir = segmA.vector() * (Math.random() + 0.1) * 1000.0;
            const segmB = new LineSegment2D(segmA.start + norm + vecDir, segmA.end + norm + vecDir);

            checkAllNotIntersects(segmA, segmB);
        }
    });

    it('should test parallel intersection', () => {
        for (let i = 0; i < 100; i++) {
            const n_x = (Math.random() + 0.1) * 10.0 * randomSign();
            const n_y = (Math.random() + 0.1) * 10.0 * randomSign();

            const e_x = (Math.random() + 0.1) * 10.0 * randomSign();
            const e_y = (Math.random() + 0.1) * 10.0 * randomSign();

            const segmA = new LineSegment2D(new Vec2D(n_x, n_y), new Vec2D(e_x, e_y));
            const vec = segmA.vector();
            const norm = new Vec2D(vec.y, vec.x).unit();
            const segmB = new LineSegment2D(
                (segmA.start.add(segmA.end)).div(2.0).sub(norm.mul(20.0)),
                (segmA.start.add(segmA.end)).div(2.0).add(norm.mul(20.0))
            );
            checkAllIntersects(segmA, segmB);
        }
    });

    function randomSign() {
        return Math.random() > 0.5 ? 1.0 : -1.0;
    }

});