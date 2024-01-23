using spacehex_csharp_client;
namespace spacehex_csharp_client_tests
{
    [TestFixture]
    public class TestGeometry
    {

        [Test]
        public void TestSegmentIntersect()
        {
            var segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
            var segmB = new LineSegment2D(new Vec2D(-1.0, 1.0), new Vec2D(1.0, -1.0));
            var pt = segmA.Intersects(segmB);
            CheckAllIntersects(segmA, segmB);
            Assert.AreEqual(true, (pt - Vec2D.ZERO).Length() < 0.01);
        }

        [Test]
        public void TestSegmentNotIntersect()
        {
            var segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
            var segmB = new LineSegment2D(new Vec2D(-2.0, -2.0), new Vec2D(-1.5, -1.5));
            CheckAllNotIntersects(segmA, segmB);
        }

        private void CheckAllNotIntersects(LineSegment2D a, LineSegment2D b)
        {
            Assert.AreEqual(true, a.Intersects(b) == null);
            Assert.AreEqual(true, a.Swap().Intersects(b) == null);
            Assert.AreEqual(true, a.Intersects(b.Swap()) == null);
            Assert.AreEqual(true, a.Swap().Intersects(b.Swap()) == null);
        }

        private void CheckAllIntersects(LineSegment2D a, LineSegment2D b)
        {
            Assert.AreEqual(true, a.Intersects(b) != null);
            Assert.AreEqual(true, a.Swap().Intersects(b) != null);
            Assert.AreEqual(true, a.Intersects(b.Swap()) != null);
            Assert.AreEqual(true, a.Swap().Intersects(b.Swap()) != null);
        }

        [Test]
        public void TestSegmentIntersectIfParallel()
        {
            var segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
            var segmB = new LineSegment2D(new Vec2D(-2.0, -2.0), new Vec2D(-1.0, -1.0));
            CheckAllIntersects(segmA, segmB);

            segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
            segmB = new LineSegment2D(new Vec2D(-2.0, -2.0), new Vec2D(2.0, 2.0));
            CheckAllIntersects(segmA, segmB);

            segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
            segmB = new LineSegment2D(new Vec2D(-0.5, -0.5), new Vec2D(2.0, 2.0));
            CheckAllIntersects(segmA, segmB);

            segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(1.0, 1.0));
            segmB = new LineSegment2D(new Vec2D(-0.5, -0.5), new Vec2D(-0.5, -0.5));
            CheckAllIntersects(segmA, segmB);
        }

        [Test]
        public void TestSegmentIntersectInEndPoint()
        {
            var segmA = new LineSegment2D(new Vec2D(-1.0, 1.0), new Vec2D(1.0, 1.0));
            var segmB = new LineSegment2D(new Vec2D(-2.0, -2.0), new Vec2D(1.0, 1.0));
            var pt = segmA.Intersects(segmB);
            CheckAllIntersects(segmA, segmB);
            Assert.AreEqual(true, (pt - new Vec2D(1.0, 1.0)).Length() < 0.01);
        }

        [Test]
        public void TestSegmentIntersectInPoint()
        {
            var segmA = new LineSegment2D(new Vec2D(-2.0, -2.0), new Vec2D(1.0, 1.0));
            var segmB = new LineSegment2D(new Vec2D(1.0, 1.0), new Vec2D(3.0, -1.0));
            var pt = segmA.Intersects(segmB);
            CheckAllIntersects(segmA, segmB);
            Assert.AreEqual(true, (pt - new Vec2D(1.0, 1.0)).Length() < 0.01);
        }

        [Test]
        public void TestSegmentNoIntersectCase()
        {
            var segmA = new LineSegment2D(new Vec2D(x: -341.0, y: -223.0), new Vec2D(x: -292.0, y: -194.0));
            var segmB = new LineSegment2D(
                new Vec2D(x: 445.8586189801885, y: 246.7999999999996),
                new Vec2D(x: 447.3242977416373, y: 243.5499999999996)
            );
            CheckAllNotIntersects(segmA, segmB);
        }

        [Test]
        public void TestSegmentNormalVectorIntersectAll()
        {
            // All normals at mid point intersect
            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var nX = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var nY = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var eX = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var eY = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var segmA = new LineSegment2D(
                    new Vec2D(x: nX, y: nY),
                    new Vec2D(x: nX + eX, y: nY + eY)
                );
                var vec = segmA.Vector();
                var norm = new Vec2D(vec.Y, vec.X).Unit();
                var segmB = new LineSegment2D(
                    (segmA.Start + segmA.End) / 2.0 - norm * 20.0,
                    (segmA.Start + segmA.End) / 2.0 + norm * 20.0
                );
                CheckAllIntersects(segmA, segmB);
            });
        }

        [Test]
        public void TestSegmentIntersectAll()
        {
            // All normals at mid point intersect
            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var nX = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var nY = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var eX = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var eY = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var segmA = new LineSegment2D(
                    new Vec2D(x: nX, y: nY),
                    new Vec2D(x: nX + eX, y: nY + eY)
                );
                var vec = segmA.Vector();
                var sign = NextSign();
                var normA = new Vec2D(vec.Y, vec.X).Unit() * sign * (new Random().NextDouble() * 100);
                var normB = new Vec2D(vec.Y, vec.X).Unit() * -sign * (new Random().NextDouble() * 100);
                var segmB = new LineSegment2D(
                    (segmA.Start + segmA.Vector() * new Random().NextDouble()) + normA,
                    (segmA.Start + segmA.Vector() * new Random().NextDouble()) + normB
                );
                CheckAllIntersects(segmA, segmB);
            });
        }

        private double NextSign()
        {
            return new Random().Next(2) == 0 ? -1.0 : 1.0;
        }

        [Test]
        public void TestSegmentParallelAndNoIntersect()
        {
            // No intersect since parallel line moved away from original
            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var nX = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var eX = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var segmA = new LineSegment2D(
                    new Vec2D(x: nX, y: nX),
                    new Vec2D(x: nX + eX, y: eX)
                );
                var norm = segmA.Vector().NormalVector() * NextSign() * ((new Random().NextDouble()) + 0.1) * 1000.0;
                var vecDir = segmA.Vector() * ((new Random().NextDouble()) + 0.1) * 1000.0;
                var segmB = new LineSegment2D(
                    new Vec2D(x: nX, y: nX) + norm + vecDir,
                    new Vec2D(x: nX + eX, y: eX) + norm + vecDir
                );
                CheckAllNotIntersects(segmA, segmB);
            });
        }

        [Test]
        public void TestParallellIntersection()
        {
            // All normals at mid point intersect
            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var nX = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var nY = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var eX = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var eY = (new Random().NextDouble() + 0.1) * 10.0 * NextSign();
                var segmA = new LineSegment2D(
                    new Vec2D(x: nX, y: nY),
                    new Vec2D(x: eX, y: eY)
                );
                var vec = segmA.Vector();
                var norm = new Vec2D(vec.Y, vec.X).Unit();
                var segmB = new LineSegment2D(
                    (segmA.Start + segmA.End) / 2.0 - norm * 20.0,
                    (segmA.Start + segmA.End) / 2.0 + norm * 20.0
                );
                CheckAllIntersects(segmA, segmB);
            });
        }

        [Test]
        public void InSegmentSingle()
        {
            var segm2 = new LineSegment2D(new Vec2D(x: -2.0, y: -1.0), new Vec2D(x: 2.0, y: 1.0));
            var pt = new Vec2D(-0.9423157439768692, -0.4711578719884346);
            Assert.AreEqual(true, segm2.InSegment(pt));
        }

        [Test]
        public void TestInSegment()
        {
            var segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(2.0, 2.0));

            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var n = (new Random().NextDouble() - 0.5) * 2;
                var pt = new Vec2D(n, n);
                Assert.AreEqual(true, segmA.InSegment(pt));
            });

            var segm2 = new LineSegment2D(new Vec2D(-2.0, -1.0), new Vec2D(2.0, 1.0));

            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var n = (new Random().NextDouble() - 0.5) * 2;
                var pt = new Vec2D(2 * n, n);
                Assert.AreEqual(true, segm2.InSegment(pt));
            });

            var segm3 = new LineSegment2D(new Vec2D(-2.0, 0.0), new Vec2D(2.0, 0.0));

            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var n = (new Random().NextDouble() - 0.5) * 2;
                var pt = new Vec2D(2 * n, 0.0);
                Assert.AreEqual(true, segm3.InSegment(pt));
            });

            var segm4 = new LineSegment2D(new Vec2D(0.0, -2.0), new Vec2D(0.0, 2.0));

            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var n = (new Random().NextDouble() - 0.5) * 2;
                var pt = new Vec2D(0.0, 2 * n);
                Assert.AreEqual(true, segm4.InSegment(pt));
            });
        }

        [Test]
        public void CaseNotInSegment()
        {
            var segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(2.0, 2.0));
            var pt = new Vec2D(-9.829257132209216, -9.829257132209216);
            Assert.AreEqual(false, segmA.InSegment(pt));

            var pt2 = new Vec2D(9.829257132209216, 9.829257132209216);
            Assert.AreEqual(false, segmA.InSegment(pt2));

            var pt3 = new Vec2D(2.02, 2.02);
            Assert.AreEqual(false, segmA.InSegment(pt3));

            var pt4 = new Vec2D(-1.01, 1.01);
            Assert.AreEqual(false, segmA.InSegment(pt4));
        }

        [Test]
        public void TestNotInSegment()
        {
            var segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(2.0, 2.0));
            Random rd = new Random();

            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var n = rd.NextDouble() + 0.1;
                var pt = new Vec2D(n, n + 0.25);
                Assert.AreEqual(false, segmA.InSegment(pt));
            });

            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var n = rd.NextDouble();
                var pt = new Vec2D(n - 10, n - 10);
                Assert.AreEqual(false, segmA.InSegment(pt));
            });

            var segm2 = new LineSegment2D(new Vec2D(-2.0, -1.0), new Vec2D(2.0, 1.0));

            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var n = (rd.NextDouble() - 0.5) * 2;
                var pt = new Vec2D(2 * n, n + 0.3);
                Assert.AreEqual(false, segm2.InSegment(pt));
            });
        }

        [Test]
        public void TestClosestPoint()
        {
            var segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(2.0, 2.0));

            var closest = segmA.ClosestPoint(new Vec2D(3.0, 3.0));
            Assert.AreEqual(true, (closest - new Vec2D(2.0, 2.0)).Length() < 0.01);

            closest = segmA.ClosestPoint(new Vec2D(-2.0, -2.0));
            Assert.AreEqual(true, (closest - new Vec2D(-1.0, -1.0)).Length() < 0.01);

            closest = segmA.ClosestPoint(new Vec2D(0.0, 0.0));
            Assert.AreEqual(true, (closest - new Vec2D(0.0, 0.0)).Length() < 0.01);

            closest = segmA.ClosestPoint(new Vec2D(0.0, 0.0));
            Assert.AreEqual(true, (closest - new Vec2D(0.0, 0.0)).Length() < 0.01);
        }

        [Test]
        public void TestClosestPointCase()
        {
            var segmA = new LineSegment2D(new Vec2D(-1.0, -1.0), new Vec2D(2.0, 2.0));
            Random rd = new Random();
            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var v = segmA.Vector();
                var n = v.NormalVector() * NextSign();

                var p = segmA.Start + v * rd.NextDouble(); // Pick a point along the line
                var check = p + n * 100.0; // Move along the normal from that point

                var closest = segmA.ClosestPoint(check);
                Assert.AreEqual(true, (closest - p).Length() < 0.01);
            });

            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var v = segmA.Vector();
                var n = v.NormalVector() * NextSign();

                var p = segmA.Start + v * (rd.NextDouble() * -100); // Pick a point

                var check = p + n * 100.0; // Move along the normal from that point

                var closest = segmA.ClosestPoint(check);
                Assert.AreEqual(true, (closest - segmA.Start).Length() < 0.01);
            });

            Enumerable.Range(0, 100).ToList().ForEach(_ =>
            {
                var v = segmA.Vector();
                var n = v.NormalVector() * NextSign();

                var p = segmA.Start + v * (1 + rd.NextDouble() * 100); // Pick a point after end
                var check = p + n * 100.0; // Move along the normal from that point

                var closest = segmA.ClosestPoint(check);
                Assert.AreEqual(true, (closest - segmA.End).Length() < 0.01);
            });
        }
    }
}